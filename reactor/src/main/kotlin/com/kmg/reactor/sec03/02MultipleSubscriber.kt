package com.kmg.udemyreactor.sec03

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

fun main() {
    val flux = Flux.just(1, 2, 3, "Tomas")

    flux.subscribe(Util.newSubscriber())
    flux.subscribe(Util.newSubscriber())

    // n 번의 subscribe 가 가능하며, 각각 다른 processor 를 가진 파이프라인을 가질 수 있다.
    flux
        .filter { it is Int && it % 2 == 0 }
        .map { it as Int * 2 }
        .subscribe(Util.newSubscriber())
}