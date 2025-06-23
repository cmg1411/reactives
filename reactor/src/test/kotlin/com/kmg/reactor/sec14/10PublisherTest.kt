package com.kmg.reactor.sec14

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import reactor.test.publisher.TestPublisher
import java.util.function.UnaryOperator
import kotlin.test.Test

/**
 * transform 에 쓰이는 공통 연산자(UnaryOperator) 를 테스트 하려면 테스트용 Publisher 가 필요하다.
 * TestPublisher 를 사용하면 테스트용 Flux 를 만들 수 있다.
 */
class `10PublisherTest` {

    private fun processor(): UnaryOperator<Flux<String>> {
        return UnaryOperator { flux: Flux<String> ->
            flux.filter { it.length > 1 }
                .map { it.uppercase() }
                .map { "$it : ${it.length}" }
        }
    }

    // TestPublisher 기본 사용법
    @Test
    fun publisherTest1() {
        val publisher = TestPublisher.create<String>()
        val flux = publisher.flux()

        flux.subscribe(Util.newSubscriber())

        publisher.emit("Hello", "World", "!")
        publisher.error(RuntimeException("Test Exception"))
    }

    @Test
    fun publisherTest2() {
        val publisher = TestPublisher.create<String>()
        val flux = publisher.flux()

        // 여기에서 emit 하면 안됨.
        // 이 때에는 subscriber 가 존재하지 않음.
        // subscriber 는 StepVerifier 의 verify() 이다.
        // 따라서 subscriber 가 존재하는 StepVerifier 의 파이프라인 안에 emit 이 존재해야 하고, then() 에서 정의해야 한다.
//        publisher.emit("hello", "world")

        StepVerifier.create(flux.transform(processor()))
            .then { publisher.emit("i", "hello", "world") } // 여기
            .expectNext("HELLO : 5")
            .expectNext("WORLD : 5")
            .verifyComplete()
    }
}
