package com.kmg.udemyreactor.sec02

import com.kmg.udemyreactor.sec01.subscriber.SubscriberImpl
import reactor.core.publisher.Mono

fun main() {
    val mono = Mono.just("test")
    val subscriber = SubscriberImpl()
    mono.subscribe(subscriber)

    subscriber.subscription.request(10) // 10개 요청했지만 Mono 라 하나만 방출된다.
}