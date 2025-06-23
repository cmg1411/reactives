package com.kmg.reactor.sec11

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

private val log = LoggerFactory.getLogger("")

// Publisher 는 onComplete, onError 를 만나면 publish 를 종료.
// retry : onError() 로 끝난 Publisher 를 다시 시작.
fun main() {
    demo2()

    Thread.sleep(5000)
}

private fun demo1() {
    getCountryName()
        .retry(2)
        .subscribe(Util.newSubscriber())
}

private fun demo2() {
    getCountryName()
        .retryWhen(retryStrategy())
        .subscribe(Util.newSubscriber())
}

private fun retryStrategy(): Retry {
//    return Retry.max(2)
    return Retry.fixedDelay(2, Duration.ofSeconds(1))
        .filter { it is RuntimeException }
        .doBeforeRetry { log.info("retrying...") }
        .onRetryExhaustedThrow(
            { retryBackoffSpec, retrySignal ->
                log.info("Retries exhausted after ${retrySignal.totalRetries()} attempts")
                RuntimeException("Retries exhausted after ${retrySignal.totalRetries()} attempts")
            }
        )
}

private fun getCountryName(): Mono<String> {
    val atomicInteger = AtomicInteger(0)
    return Mono.fromSupplier {
        if (atomicInteger.incrementAndGet() < 5) {
            throw RuntimeException("Error occurred")
        }
        Util.faker.country().name()
    }.doOnError { log.info("Error ${it.message}") }
        .doOnSubscribe { log.info("Subscribing") }
}