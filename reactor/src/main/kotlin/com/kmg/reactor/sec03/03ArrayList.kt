package com.kmg.udemyreactor.sec03

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

fun main() {

    val list = listOf(1, 2, 3, 4, 5)

    // just 에 리스트 넘기면 리스트 객체를 넘긴다. fromArray, fromIterable 사용하자.
    Flux.just(list)
        .subscribe(Util.newSubscriber())

    // 1. just + 코틀린 전개연산자 사용
    Flux.just(*list.toTypedArray()) // * 전개연산자
        .subscribe(Util.newSubscriber())

    // 2. fromArray
    Flux.fromArray(list.toTypedArray())
        .subscribe(Util.newSubscriber())

    // 3. fromIterable
    Flux.fromIterable(list)
        .subscribe(Util.newSubscriber())

    // 필터링 예제
    Flux.fromIterable(list)
        .filter { it % 2 == 0 }
        .map { it * 2 }
        .subscribe(Util.newSubscriber())
}