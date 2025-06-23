package com.kmg.reactor.sec14

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import reactor.test.StepVerifierOptions

/**
 * StepVerifierOptions 는 테스트 옵션을 줄 수 있다.
 * 그 중 scenarioName() 는 테스트의 시나리오 이름을 지정할 수 있다.
 * 이는 에러가 났을때 표시 되어, 정보 전달에 유용하다.
 *
 * 또한 as() 를 사용 하여 각 단계에 대한 설명을 추가할 수 있다.
 * 이 또한 에러시 메세지가 표시 되어, 정보 전달에 유용하다.
 */
class `08ScenarioNameTest` {
    private fun getItems() = Flux.range(1, 3)

    @Test
    fun scenarioNameTest() {
        val options = StepVerifierOptions.create().scenarioName("1 to 3 items test")

        StepVerifier.create(getItems(), options)
            .expectNext(11)
            .`as`("expect 1st item should be 1")
            .expectComplete()
            .verify()
    }

    @Test
    fun scenarioNameTest2() {
        val options = StepVerifierOptions.create().scenarioName("1 to 3 items test")

        StepVerifier.create(getItems(), options)
            .expectNext(1)
            .`as`("expect 1st item should be 1")
            .expectNext(2, 4)
            .`as`("expect second, third item should be 2 and 3")
            .expectComplete()
            .verify()
    }
}