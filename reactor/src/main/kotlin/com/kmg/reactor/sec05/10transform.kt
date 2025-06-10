package com.kmg.udemyreactor.sec05

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import java.util.function.UnaryOperator

private val log = LoggerFactory.getLogger("10transform")

// transform : 공통 operator 를 함수처럼 한데 묶어서 사용할 수 있는 기능
// Flux 타입의 UnaryOperator(인자와 리턴이 같은 타입인 Function) 를 반환해야 한다.
fun main() {

    val debugEnabled = true
    val transform: UnaryOperator<Flux<String>> = if(debugEnabled) addDebugger() else UnaryOperator.identity()

    getCustomers()
        .transform(transform)
        .subscribe()

    getProducts()
        .transform(transform)
        .subscribe()
}

fun getCustomers(): Flux<String> {
    return Flux.range(1, 10)
        .map { Util.faker.name().firstName() }
}

fun getProducts(): Flux<String> {
    return Flux.range(1, 10)
        .map { Util.faker.commerce().productName() }
}

fun <T> addDebugger(): UnaryOperator<Flux<T>> {
    return UnaryOperator { flux ->
        flux
            .doOnSubscribe { log.info("Subscribed") }
            .doOnNext { log.info("Next: $it") }
            .doOnComplete { log.info("Completed") }
            .doOnError { log.info("Error: $it") }
    }
}