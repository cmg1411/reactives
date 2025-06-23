package com.kmg.reactor.sec14

import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

/**
 * verifyComplete() == expectComplete().verify()
 * verifyError() == expectError().verify()
 */
class `03Verify` {

    private fun getUsername(userId: Int): Mono<String> {
        return when(userId) {
            1 -> Mono.just("tomas")
            2 -> Mono.empty()
            else -> Mono.error(IllegalArgumentException("Invalid userId: $userId"))
        }
    }


    @Test
    fun successTest() {
        // empty 테스트 : 바로 expectComplete()
        StepVerifier.create(getUsername(2))
            .verifyComplete()
//            .expectComplete().verify()
    }

    @Test
    fun errorTest() {
        // empty 테스트 : 바로 expectComplete()
        StepVerifier.create(getUsername(3))
            .verifyError()
//            .expectError().verify()
    }
}