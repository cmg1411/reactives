package com.kmg.reactor.sec14

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

private val log = LoggerFactory.getLogger("")

// StepVerifier 는 리엑터 테스트를 위해 있다.
class `01MonoTest` {

    @Test
    fun test() {
        StepVerifier.create(getProduct(1))
            .expectNext("product - 1")
//            .expectNext("product - 2") // fail
            .expectComplete()
            .verify() // subscriber
    }
}

private fun getProduct(id: Int): Mono<String> {
    return Mono.fromSupplier { "product - $id" }
        .doFirst { log.info("invoked") }
}