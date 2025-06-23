package com.kmg.reactor.sec13

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.context.Context

private val log = LoggerFactory.getLogger("")

// 기본적으로 multi producer 를 사용해도 context 는 전파된다.
fun main() {
    welcomeMessage()
        .concatWith(
            Flux.merge(
                // 이 producer 는 boundedElastic 스케줄러에서 실행되지만 Context 가 전파되어 로그가 찍힌다.
                producer1(),
                // 이 producer 도 parallel 스케줄러에서 실행되지만 기본적으로 Context 가 전파되어 로그가 찍힌다.
                // 만약 그러고 싶지 않으면 이렇게 오버라이드 하면 된다.
                producer2().contextWrite { Context.empty() },
            )
        )
        .contextWrite(Context.of("user", "tomas", "key2", "value2"))
        .subscribe(Util.newSubscriber())

    Thread.sleep(3000)
}

private fun welcomeMessage(): Mono<String> {
    return Mono.deferContextual { ctx ->
        if (ctx.hasKey("user")) {
            Mono.just("Welcome back, ${ctx.getOrDefault("user", "Guest")}")
        } else {
            Mono.error(IllegalArgumentException("User not found in context"))
        }
    }
}

private fun producer1(): Mono<String> {
    return Mono.deferContextual { ctx ->
        log.info("producer1: $ctx")
        Mono.empty<String>()
    }.subscribeOn(Schedulers.boundedElastic())
}

private fun producer2(): Mono<String> {
    return Mono.deferContextual { ctx ->
        log.info("producer2: $ctx")
        Mono.empty<String>()
    }.subscribeOn(Schedulers.parallel())
}