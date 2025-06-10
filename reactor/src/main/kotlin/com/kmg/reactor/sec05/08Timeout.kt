package com.kmg.udemyreactor.sec05

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.TimeoutException

// timeout(duration) : publisher 의 동작을 duration 만큼 기다린 후 timeoutException 발생.
    // onErrorReturn 등으로 핸들링.
// timeout(duration, fallback) : publisher 의 동작을 duration 만큼 기다린 후 fallback 실행(구독).
    // fallback 은 발생하기전에는 구독하지 않는 publisher 이므로 발생하지 않으면 실행되지 않음.
fun main() {
    getProductName()
//        .timeout(Duration.ofSeconds(1))
        .timeout(Duration.ofSeconds(1), getProductNameFallback())
        .onErrorReturn(TimeoutException::class.java, "error default name when timeout")
        .onErrorReturn("error default name")
        .subscribe(Util.newSubscriber())

    Thread.sleep(5000)
}

fun getProductName(): Mono<String> {
    return Mono.fromSupplier { "service - ${Util.faker.commerce().productName()}" }
        .delayElement(Duration.ofSeconds(2))
}

fun getProductNameFallback(): Mono<String> {
    return Mono.fromSupplier { "fallback - ${Util.faker.commerce().productName()}" }
        .delayElement(Duration.ofMillis(200))
}
