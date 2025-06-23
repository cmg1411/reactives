package com.kmg.reactor.sec14

import com.kmg.udemyreactor.common.Util
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class `05-2RandomTest` {

    private fun getRandomItems() = Flux.range(1, 50)
        .map { Util.faker.random().nextInt(1, 100) }

    @Test
    fun randomTest() {
        StepVerifier.create(getRandomItems())
            .expectNextMatches { it in 1..100 } // 첫번째 값이 1~100 사이인지 확인
            .expectNextCount(49)
            .expectComplete()
            .verify()
    }
}