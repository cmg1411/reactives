package com.kmg.reactor.sec14

import com.kmg.udemyreactor.common.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class Book(
    val id: Int,
    val author: String,
    val title: String,
)

/**
 * expect~() 시리즈는 객체 자체 비교를 해준다.
 * 객체에서 특정 필드만 검사하고 싶다면? assertNext() 를 사용하여 직접 Assertion 을 정의 한다.
 */
class `06AssertNextTest` {

    private fun getBooks() = Flux.range(1, 4)
        .map { Book(it, Util.faker.book().author(), Util.faker.book().title()) }

    @Test
    fun assertNextTest() {
        StepVerifier.create(getBooks())
            .assertNext { Assertions.assertEquals(1, it.id) }
            .thenConsumeWhile { it.title.isNotEmpty() }
            .expectComplete()
            .verify()
    }

    @Test
    fun collectAllAndTest() {
        StepVerifier.create(getBooks().collectList())
            .assertNext { Assertions.assertEquals(it.size, 4) }
            .expectComplete()
            .verify()
    }
}