package com.kmg.udemyreactor.sec05

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

fun main() {

    // switchIfEmpty : Flux.empty() 또는 Mono.empty() 를 방출하는 경우 다른 Publisher 로 대체 가능.
    // redis 캐시 조회 후 없으면 db 조회 이런 동작 구현 가능.
    Flux.range(1, 10)
        .filter { it > 10 }
        .switchIfEmpty(
            Flux.range(1, 5)
                .map { it * 10 }
        )
        .subscribe(Util.newSubscriber())
}