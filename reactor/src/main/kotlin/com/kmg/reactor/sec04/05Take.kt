package com.kmg.udemyreactor.sec04

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

fun main() {
    // take
    // takeWhile : 조건식 만족하면 계속
    // takeUntil : 조건식 만족할 때 까지 계속
    Flux.range(1, 10)
        .log("take")
        .take(3)
        .log("subscriber")
        .subscribe(Util.newSubscriber())

    Flux.range(1, 10)
        .log("take")
        .takeWhile { it < 5 }
        .log("subscriber")
        .subscribe(Util.newSubscriber())

    Flux.range(1, 10)
        .log("take")
        .takeUntil{ it == 5 }
        .log("subscriber")
        .subscribe(Util.newSubscriber())
}