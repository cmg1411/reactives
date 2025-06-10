package com.kmg.udemyreactor.sec05

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val log = LoggerFactory.getLogger("error")

fun main() {
    // onErrorReturn 은 예외가 발생했을때 catch 하여 대체할 반환값을 지정할 수 있다.
    // stream 의 흐름에 따라 예외가 발생할만한 publisher(operator) 아래에 존재해야 한다. subscriber 바로 위가 좋다.
    // onErrorReturn 끼리는 선언 순서에 대한 우선순위가 없다. 예외 타입에 대한 검사 후
    // 타입 인자 없는 onErrorReturn 이 else 처럼 동작한다.
    Flux.range(1, 10)
        .map { if(it == 5) it/0 else it }
        .onErrorReturn(IllegalArgumentException::class.java, -1)
        .onErrorReturn(ArithmeticException::class.java, -2)
        .onErrorReturn(-3)
        .subscribe(Util.newSubscriber())

    // onErrorResume 은 예외 발생시 대체할 publisher 를 지정할 수 있다.
    // onErrorReturn, onErrorResume 를 섞어서 여러 에러 처리 논리를 만들 수 있다.
    Flux.range(1, 10)
        .map { if(it == 5) it/0 else it }
        .onErrorResume(ArithmeticException::class.java) { fallback1() }
        .onErrorResume(IllegalArgumentException::class.java) { fallback2() }
        .onErrorReturn(-3)
        .subscribe(Util.newSubscriber())

    // onErrorComplete
    Flux.range(1, 10)
        .map { if(it == 5) it/0 else it }
        .onErrorComplete()
        .subscribe(Util.newSubscriber())

    // onErrorContinue : 에러 발생했을때 인자로 정의된 consumer 를 실행하고 넘어간다.
    // onErrorStop : 다운스트림에 onErrorContinue 가 있을때, 그걸 무시할 수 있다. 업스트림에서 다운스트림의 전략을 오버라이드할 때 사용.
    Flux.range(1, 10)
        .map { if(it == 5) it/0 else it }
//        .onErrorStop()
        .onErrorContinue { error, item ->
            log.error("error: $error, item: $item")
        }
        .subscribe(Util.newSubscriber())
}

fun fallback1(): Mono<Int> {
    return Mono.fromSupplier { Util.faker.random().nextInt(100, 1000) }
}

fun fallback2(): Mono<Int> {
    return Mono.fromSupplier { Util.faker.random().nextInt(1000, 10000) }
}
