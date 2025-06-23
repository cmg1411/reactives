package com.kmg.reactor.sec14

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

/**
 * range 는 순차 증가 하는 Flux.
 * 따라서 처음 몇개만 체크한 후, 이후 데이터는 갯수만 체크하는 식으로 테스트한다.
 * expectNext() 에 첫 몇가지 데이터를 테스트하고,
 * 나머지에 대해선 expectNextCount() 로 개수만 테스트.
 *
 */
class `05-1RangeTest` {

    private fun getItems() = Flux.range(1, 50)

    @Test
    fun rangeTest1() {
        StepVerifier.create(getItems())
            .expectNext(1, 2, 3, 4, 5)
            .expectNextCount(45) // 6부터 50까지의 나머지 45개를 기대
            .expectComplete()
            .verify()
    }

    // 중간 값도 이런 식으로 테스트 가능
    @Test
    fun rangeTest2() {
        StepVerifier.create(getItems())
            .expectNext(1, 2, 3, 4, 5)
            .expectNextCount(20)
            .expectNext(26, 27, 28)
            .expectNextCount(22)
            .expectComplete()
            .verify()
    }
}
