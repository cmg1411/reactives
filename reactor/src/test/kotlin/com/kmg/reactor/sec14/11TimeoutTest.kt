package com.kmg.reactor.sec14

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration

/**
 * 특정 publish 가 정해진 시간 안에 완료되어야 한다면,
 * verify(Duration) 메서드를 사용하여 타임아웃을 걸 수 있다.
 */
class `11TimeoutTest` {
    // 총 1000ms
    private fun getItems() = Flux.range(1, 5)
        .delayElements(Duration.ofMillis(200))

    @Test
    fun timeoutTest() {
        StepVerifier.create(getItems())
            .expectNext(1, 2, 3, 4, 5)
            .expectComplete()
//            .verify(Duration.ofMillis(500)) // fail
            .verify(Duration.ofMillis(1100)) // success
    }
}