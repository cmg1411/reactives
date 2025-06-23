package com.kmg.reactor.sec14

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration

class `07VirtualTimeTest` {

    private fun getItems() = Flux.range(1, 5)
        .delayElements(Duration.ofSeconds(10))

    @Test
    fun virtualTimeTest1() {
        StepVerifier.withVirtualTime { getItems() }
            // 5개의 아이템이 10초 간격으로 방출되므로, 총 50초가 걸린다. 1초 여유에 51초가 걸린다고 선언.
            // 실제로 기다리진 않는다.
            .thenAwait(Duration.ofSeconds(51))
            .expectNext(1, 2, 3, 4, 5)
            .expectComplete()
            .verify()
    }

    @Test
    fun virtualTimeTest2() {
        StepVerifier.withVirtualTime { getItems() }
            .expectSubscription()
            // 첫 이벤트는 10초 후에 방출되므로
            // 첫 9초는 아무것도 방출되지 않는다는 것을 테스트 가능.
            // 다만 구독 이벤트는 있을 것이므로 위의 expectSubscription() 은 필요하다.
            .expectNoEvent(Duration.ofSeconds(9))
            .thenAwait(Duration.ofSeconds(1))
            .expectNext(1)
            .thenAwait(Duration.ofSeconds(40))
            .expectNext(2, 3, 4, 5)
            .expectComplete()
            .verify()
    }
}