package com.kmg.udemyreactor.sec03

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

fun main() {
    Flux.empty<String>()
        .subscribe(Util.newSubscriber())

    Flux.error<String>(IllegalArgumentException("oops"))
        .subscribe(Util.newSubscriber())
}