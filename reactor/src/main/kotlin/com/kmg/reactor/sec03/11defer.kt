package com.kmg.udemyreactor.sec03

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

fun main() {
    Flux.defer { lazyList() }
        .subscribe(Util.newSubscriber<Int>())
}

fun lazyList(): Flux<Int> {
    return Flux.range(1, 10)
        .map { it * 2 }
}
