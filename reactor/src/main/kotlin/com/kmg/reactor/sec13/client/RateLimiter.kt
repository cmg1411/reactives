package com.kmg.reactor.sec13.client

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*

object RateLimiter {

    private val categoryAttempts: MutableMap<String, Int> = Collections.synchronizedMap(mutableMapOf())

    fun <T> limitCalls(): Mono<T> {
        return Mono.deferContextual { ctx ->
            val allowCall = ctx.getOrEmpty<String>("category")
                .map(RateLimiter::canAllow)
                .orElse(false)
            if (allowCall) Mono.empty() else Mono.error(RuntimeException("exceeded the given limit"))
        }
    }

    @Synchronized
    private fun canAllow(category: String): Boolean {
        val attempts = categoryAttempts.getOrDefault(category, 0)
        if (attempts > 0) {
            categoryAttempts[category] = attempts - 1
            return true
        }
        return false
    }

    fun refresh() {
        Flux.interval(Duration.ofSeconds(5)) // 5초마다 rate limiter 토큰 넣어주는 프로듀서
            .startWith(0L)
            .subscribe { i ->
                categoryAttempts["standard"] = 2
                categoryAttempts["prime"] = 3
            }
    }
}

