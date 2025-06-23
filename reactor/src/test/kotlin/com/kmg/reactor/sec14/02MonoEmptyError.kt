package com.kmg.reactor.sec14

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

// empty 를 예상 한다면 expect~() 자체를 안쓰면 된다.
class `02MonoEmptyError` {

    private fun getUsername(userId: Int): Mono<String> {
        return when(userId) {
            1 -> Mono.just("tomas")
            2 -> Mono.empty()
            else -> Mono.error(IllegalArgumentException("Invalid userId: $userId"))
        }
    }

    @Test
    fun emptyTest() {
        // empty 테스트 : 바로 expectComplete()
        StepVerifier.create(getUsername(2))
            .expectComplete()
            .verify()
    }

    /**
     * Error case1
     * 기본 케이스. 모든 예외 체크.
     */
    @Test
    fun errorTest1() {
        StepVerifier.create(getUsername(3))
            .expectError()
            .verify()
    }

    /**
     * Error case2
     * 특정 예외 타입 체크.
     */
    @Test
    fun errorTest2() {
        StepVerifier.create(getUsername(3))
            .expectError(IllegalArgumentException::class.java)
            .verify()
    }

    /**
     * Error case3
     * 예외 메세지 체크.
     */
    @Test
    fun errorTest3() {
        StepVerifier.create(getUsername(3))
            .expectErrorMessage("Invalid userId: 3")
            .verify()
    }

    /**
     * Error case4
     * 예외 타입과 메세지를 체크하고 싶다고 expectError(), expectErrorMessage()를 같이 쓸 수 없다.
     * 이미 예외가 터진 이상 다음 operation 이 실행되지 않기 때문.
     * 그럴땐 consumeErrorWith() 를 한 후 junit 의 assertion 으로 모두 체크해주기.
     */
    @Test
    fun errorTest4() {
        StepVerifier.create(getUsername(3))
            .consumeErrorWith {
                Assertions.assertEquals(IllegalArgumentException::class.java, it.javaClass)
                Assertions.assertEquals("Invalid userId: 3", it.message)
            }
//            .expectError(IllegalArgumentException::class.java)
//            .expectErrorMessage("Invalid userId: 3")
            .verify()
    }
}
