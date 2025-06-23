package com.kmg.reactor.sec14

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.StepVerifierOptions
import reactor.util.context.Context

/**
 * StepVerifier 에서 실행되는 Publisher 에 Context 를 주고 싶다면 StepVerifierOptions 의 withInitialContext() 를 사용한다.
 */
class `09ContextTest` {

    private fun getWelcomeMessage(): Mono<String> {
        return Mono.deferContextual { ctx ->
            if (ctx.hasKey("user")) {
                Mono.just("Welcome ${ctx.get<String>("user")}")
            } else {
                Mono.error(IllegalArgumentException("Name not found in context"))
            }
        }
    }

    @Test
    fun welcomeMessageTest1() {
        val options = StepVerifierOptions.create().withInitialContext(Context.of("user", "tomas"))

        StepVerifier.create(getWelcomeMessage(), options)
            .expectNext("Welcome tomas")
            .expectComplete()
            .verify()
    }

    @Test
    fun welcomeMessageTest2() {
        val options = StepVerifierOptions.create().withInitialContext(Context.empty())

        StepVerifier.create(getWelcomeMessage(), options)
            .consumeErrorWith {
                Assertions.assertEquals(it.javaClass, IllegalArgumentException::class.java)
                Assertions.assertEquals(it.message, "Name not found in context")
            }
            .verify()
    }
}