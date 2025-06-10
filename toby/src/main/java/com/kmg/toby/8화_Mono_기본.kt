package com.kmg.toby

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.util.StopWatch
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private val log = mu.KotlinLogging.logger {}

@RestController
class `8화_Mono_기본`(
    private val myService: MyService
) {

    val client = WebClient.create()
    val REMOTE_SLOW_URI = "http://localhost:8081/remote/service-slow?req="
    val REMOTE_SLOW2_URI = "http://localhost:8081/remote/service-slow2?req="

    @GetMapping("/mono")
    fun mono(idx: String): Mono<String> {
        // 이 코드는 바로 실행이 되지 않는다. 단순 Mono 를 리턴한다.
        // publisher 이기 때문이다.
        // Subscribe 를 해야 비로소 실행된다.
        // 컨트롤러에서 Mono 를 리턴하면 Subscriber 를 스프링이 자동을 만들어서 구독시킨다.
        return client.get().uri("$REMOTE_SLOW_URI${idx}").exchange()
            .flatMap { it.bodyToMono<String>() }
            .flatMap { client.get().uri("$REMOTE_SLOW2_URI${it}").exchange() }
            .flatMap { it.bodyToMono<String>() }
            .doOnNext { log.info(it) } // reactor-http-nio
//            .map { myService.work(it) } // 그냥 string 리턴 메서드니 map 하면 됨. // 앞선 작업의 스레드에서 work 가 동작한다.
            .flatMap { Mono.fromCompletionStage(myService.workAsync(it)) } // @Async 를 이용해서 따로의 스레드에서 workAsync 동작하도록.
            .doOnNext { log.info(it) } // thread-task

        /**
         * Mono 안에 담긴 값에 어떤 함수를 적용. (Stream 과 비슷한 개념)
         * Mono 의 멤버함수 map, flatMap
         * - map : 모노 안에 담긴 값의 변형
         * - flatMap : 모노 안에 담긴 값을 변형하는데, 변형한 값이 Mono 라 Mono<Mono<>> 가 될 떄 flat 해줌(Mono<> 로 만듦)
         *
         * 즉 Mono<String> 이면 map, flatMap 블럭 안의 it 은 String
         */
        /**
         * 스레드 컨트롤
         * https://docs.spring.io/spring-framework/reference/web/webflux/new-framework.html#webflux-concurrency-model
         * Mono 에 정의된 작업이 순차적으로 실행되고,
         * 이전의 작업이 실행된 스레드에서 다음 작업이 실행된다.
         * ex) 위 코드에서 webclient 작업 -> myService 작업의 경우, webclient 의 reactor-http-nio 스레드에서 myService 를 처리하게 된다.
         *     만약 myService 의 내용이 오래 결린다면, weblient 의 스레드를 오래 잡고 있게 된다.
         *     webclient 는 nio 스레드 풀에서 하나의 스레드를 잃었으므로 스레드 하나만큼 일을 못할 것..
         *
         * .doOnNext { log.info(it) } 를 하고 실행된 스레드를 확인해보자.
         *
         * 해결책
         * 1. myService 작업을 async 로 따로 스레드 풀을 정의.
         * 2. 스케줄러를 따로 정의해준다. (publishOn, subscribeOn)
         *
         */
    }
}

@Service class MyService {
    @Async
    fun workAsync(req: String): CompletableFuture<String> {
        log.info { "[${Thread.currentThread()}] workAsync" }
        return CompletableFuture.completedFuture("$req/asyncWork")
    }

    fun work(req: String): String {
        log.info { "[${Thread.currentThread()}] work" }
        return "$req/asyncWork"
    }
}

fun main() {
    performanceTest("http://localhost:8080/mono?idx=1")
}

fun performanceTest(url: String) {
    val counter = AtomicInteger()
    val es = Executors.newFixedThreadPool(100)

    val restTemplate = RestTemplate()

    val main = StopWatch()
    main.start()

    (1..100).forEach {
        es.execute {
            val idx = counter.addAndGet(1)
            log.info { "Thread $idx" }

            val sw = StopWatch()
            sw.start()

            val result = restTemplate.getForObject(url, String::class.java)
            log.info { "Result $idx : $result" }

            sw.stop()
            log.info { "Elapsed $idx : ${sw.totalTimeSeconds}" }
        }
    }

    es.shutdown()
    es.awaitTermination(100, TimeUnit.SECONDS)

    main.stop()
    log.info { "Total : ${main.totalTimeSeconds}" }
}