package com.kmg.reactor.sec13

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.util.context.Context

private val log = LoggerFactory.getLogger("")

// reactor Context 는 Http 헤더와 같이 추가 메타데이터 정보를 전달한다.
// MVC 에는 ThreadLocal 이 있으나, 스레드가 바뀌는 환경에서는 쓸 수 없으며 Context 를 사용한다.
// deferContextual 로 Context 를 가져와 사용할 수 있다.
// 함수 인자로 전달되는게 아니므로 시그니쳐를 바꿀 필요 없다.
fun main() {
    welcomeMessage()
        .contextWrite(Context.of("user", "tomas", "key2", "value2"))
        .subscribe(Util.newSubscriber())
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
