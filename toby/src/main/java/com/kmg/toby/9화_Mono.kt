package com.kmg.toby

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

private val log = mu.KotlinLogging.logger {}

@RestController
class `9화_Mono` {

    /**
     * [reactor-http-nio-2] pos1
     * [reactor-http-nio-2] pos2
     * [reactor-http-nio-2] onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
     * [reactor-http-nio-2] request(unbounded) : unbounded -> 다 보내 !
     * [reactor-http-nio-2] Hello, world!
     * [reactor-http-nio-2] onNext(Hello, world!)
     * [reactor-http-nio-2] onComplete()
     *
     * 요점 1 -> Pub/Sub 과정.
     * 요점 2 -> pos1, pos2 가 먼저 실행.
     *  - Mono 는 Publisher 임. 누군가 Subscribe 하지 않으면 실행되지 않음.
     *  - RestController 메서드가 끝나고 나서 Spring 이 만들어주는 Subscriber 가 등록되면 실행되므로, pos1, pos2가 먼저 실행 되었다.
     * 요점 3 -> 모든 작업이 reactor-http-nio-2 스레드에서 실행됨.
     *  - Mono 는 특별한 작업이 없으면 기본적으로 동기적으로 실행된다.
     *  - 다만 Mono 블럭에서 정의한 작업들은 실제로 실행되지 않는다는 것. (실제 실행되는것도 아니니 non-blocking 이라고는 할 수 없는듯.)
     */
    @GetMapping("/")
    fun hello(): Mono<String> {
        log.info { "pos1" }
        val res = Mono.just("Hello, world!").doOnNext(log::info).log()
        log.info { "pos2" }
        return res
    }

    /**
     * [reactor-http-nio-2] pos1
     * [reactor-http-nio-2] method generateLog() // 이 로그 타이밍
     * [reactor-http-nio-2] pos2
     * [reactor-http-nio-2] onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
     * [reactor-http-nio-2] request(unbounded)
     * [reactor-http-nio-2] Generate Log
     * [reactor-http-nio-2] onNext(Generate Log)
     * [reactor-http-nio-2] onComplete()
     *
     * Mono 에 등록된 operator 작업 들은 위에 언급 했듯 뒤에 실행 되지만, Mono.just() 안의 작업은 먼저 처리 된다는 것을 알 수 있다.
     * just 는 값을 만드는 것이기 때문.
     */
    @GetMapping("/fun-in-just")
    fun hello2(): Mono<String> {
        log.info { "pos1" }
        val res = Mono.just(generateLog()).doOnNext(log::info).log()
        log.info { "pos2" }
        return res
    }

    /**
     * [reactor-http-nio-2] pos1
     * [reactor-http-nio-2] pos2
     * [reactor-http-nio-2] onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
     * [reactor-http-nio-2] request(unbounded)
     * [reactor-http-nio-2] method generateLog()
     * [reactor-http-nio-2] Generate Log
     * [reactor-http-nio-2] onNext(Generate Log)
     * [reactor-http-nio-2] onComplete()
     *
     * Mono 의 시작 작업도 나중으로 미루고 싶으면, fromSupplier 를 사용하면 된다.
     */
    @GetMapping("/fun-in-supply")
    fun hello3(): Mono<String> {
        log.info { "pos1" }
        val res = Mono.fromSupplier(::generateLog).doOnNext(log::info).log()
        log.info { "pos2" }
        return res
    }

    /**
     * Mono/Flux 는 Publisher 이므로 복수개의 Subscriber 가 등록될 수 있다.
     * 아래 코드는 Cold Publisher 이므로 새로 구독할 때 마다 처음부터 모든 데이터를 보낸다.
     * https://vinsguru.medium.com/java-reactive-programming-cold-publisher-vs-hot-publisher-610933f06042
     */
    @GetMapping("/sub-twice")
    fun hello4(): Mono<String> {
        log.info { "pos1" }
        val res = Mono.just("Hello.")
            .flatMap { WebClient.create().get().uri("http://localhost:8081/remote/service-slow?req=$it").exchange() }
            .flatMap { it.bodyToMono(String::class.java) }
            .doOnNext { log.info { it } }
            .log()

        res.subscribe()
        log.info { "pos2" }
        return res
    }

    @GetMapping("/block-and-sub")
    fun hello5(): Mono<String> {
        log.info { "pos1" }
        val res = Mono.just("Hello.")
            .flatMap { WebClient.create().get().uri("http://localhost:8081/remote/service-slow?req=$it").exchange() }
            .flatMap { it.bodyToMono(String::class.java) }
            .doOnNext { log.info { it } }
            .log()

        val result = res.block()
        log.info { "pos2 $result" }
        return Mono.just(result!!) // 이미 값이 있으면 Mono.just() 로 감싸서 리턴. res 를 리턴하면 구독이 한번 더 불필요하게 실행됨.
    }

    fun generateLog(): String {
        log.info { "method generateLog()" }
        return "Generate Log"
    }
}

fun main() {
    Mono.fromSupplier {  }
        .block()
}
