package com.kmg.udemyreactor.sec03

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

fun main() {
    Flux.just(1, 2, 3, "Tomas")
        .subscribe(Util.newSubscriber())
}