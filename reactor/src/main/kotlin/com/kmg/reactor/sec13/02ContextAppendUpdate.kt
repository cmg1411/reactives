package com.kmg.reactor.sec13

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.util.context.Context

private val log = LoggerFactory.getLogger("")

// contextWrite 도 아래에서 위로 순서대로 적용된다.
// Function<Context, Context> contextModifier 를 인자로 받는 함수가 update 용
// ContextView contextToAppend 를 인자로 받는 함수가 append 용
fun main() {
    welcomeMessage()
//        .contextWrite { ctx -> Context.empty() }
        .contextWrite { ctx -> ctx.put("user", ctx.get<Any?>("user").toString().uppercase()) } // update
        .contextWrite(Context.of("appendedKey1", "appendedValue1").put("appendedKey2", "appendedValue2")) // append
        .contextWrite(Context.of("user", "tomas", "key2", "value2"))
        .subscribe(Util.newSubscriber())
}

private fun welcomeMessage(): Mono<String> {
    return Mono.deferContextual { ctx ->
        log.info("ctx : $ctx")

        if (ctx.hasKey("user")) {
            Mono.just("Welcome back, ${ctx.getOrDefault("user", "Guest")}")
        } else {
            Mono.error(IllegalArgumentException("User not found in context"))
        }
    }
}
