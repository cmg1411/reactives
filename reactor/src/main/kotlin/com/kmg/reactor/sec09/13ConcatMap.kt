package com.kmg.reactor.sec09

import reactor.core.publisher.Flux

// concatMap 은 flatMap 과 비슷하지만, 순서를 보장한다.
// concatWith 을 순서대로 쭉 쓴 동작과 같다.
fun main() {
    Flux.range(1, 5)
        // concatMap 과 flatMap 의 출력 차이를 보자.
        .concatMap { i ->
//        .flatMap { i ->
            Flux.range(1, i)
                .map { j -> "i: $i, j: $j" }
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
        }
        .subscribe { println(it) }

    Thread.sleep(3000)
}