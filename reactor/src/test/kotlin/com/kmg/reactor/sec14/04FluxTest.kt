package com.kmg.reactor.sec14

import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import kotlin.test.Test

/**
 * flux 는 여러 값을 방출하기에,
 * StepVerifier.create() 에서 두번째 인자로 요청할 수 있는 개수를 지정할 수 있다.
 * 이 때 flux 가 방출할 수 있는 개수보다 적은 요청을 한 상태에서, expectComplete() 를 했다면 무한정 기다리게 되니 주의.
 * 원하는 만큼 request 하여 테스트 한 후 Flux 를 종료하려면 thenCancel() 를 사용한다.
 *
 * Flux 에서 방출되는 여러 값을 테스트 하려면 expectNext() 를 여러 번 사용하거나,
 * expectNext() 의 인자로 여러 값을 전달할 수 있다.
 */
class `04FluxTest` {

    private fun getItems() = Flux.just("a", "b", "c").log()

    @Test
    fun fluxTest1() {
        StepVerifier.create(getItems(), 1)
            .expectNext("a")
            .thenCancel()
//            .expectComplete() // 이건 무한정 기다림. 쓰면 안됨.
            .verify()
    }

    @Test
    fun fluxTest2() {
        StepVerifier.create(getItems())
            .expectNext("a")
            .expectNext("b")
            .expectNext("c")
            .expectComplete()
            .verify()
    }

    @Test
    fun fluxTest3() {
        StepVerifier.create(getItems())
            .expectNext("a", "b", "c")
            .expectComplete()
            .verify()
    }
}