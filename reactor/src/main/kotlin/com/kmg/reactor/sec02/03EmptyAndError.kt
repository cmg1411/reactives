package com.kmg.udemyreactor.sec02

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Mono

fun main() {
    getUsername(1).subscribe(Util.newSubscriber("EmptyError"))
    getUsername(2).subscribe(Util.newSubscriber("EmptyError"))
    getUsername(3).subscribe(Util.newSubscriber("EmptyError"))
}

fun getUsername(userId: Int): Mono<String> {
    return when(userId) {
        1 -> Mono.just("kim")
        2 -> Mono.empty()
        else -> Mono.error(RuntimeException("invalid input !!"))
    }
}