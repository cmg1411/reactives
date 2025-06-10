package com.kmg.udemyreactor.sec12

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Sinks

private val log = LoggerFactory.getLogger("multicast")

// Sinks.many().multicast() 는 여러 subscriber 를 받을 수 있다.
// late subscriber 는 메세지를 받을 수 없음. (unicast 와 다르게 메모리에 저장되지 않음)
// warmup 과정이 있다.

// 이 챕터는 multicast 의 onBackpressureBuffer 에 대한 설명이다.
// 특징으로,
// 1. unicast 와 다르게 기본 queue 가 Queues.SMALL_BUFFER_SIZE 크기의 bounded queue 임
// 2. warmup : 첫 subscriber 가 붙었을 때 기존에 존재하던 큐의 모든 메세지를 한번 구독하는 과정이 있음.
fun main() {
//    demo6()
    demo7()
}

fun demo6() {
    // multicast 의 onBackpressureBuffer 는 unicast 와 다르게 기본이 Queues.SMALL_BUFFER_SIZE bounded queue 임
    val sink = Sinks.many().multicast().onBackpressureBuffer<String>()
    val flux = sink.asFlux()

    flux.subscribe(Util.newSubscriber("foo"))
    flux.subscribe(Util.newSubscriber("bar"))

    sink.tryEmitNext("Hello")
    sink.tryEmitNext("How are you?")
    sink.tryEmitNext("Where are you from?")

    Thread.sleep(1000)

    // 늦게 온 sam subscriber 는 Late Message 만 받고, 앞의 메세지는 받을 수 없다.
    flux.subscribe(Util.newSubscriber("sam"))
    sink.tryEmitNext("Late Message")
}

fun demo7() {
    // multicast 의 onBackpressureBuffer 는 unicast 와 다르게 기본이 Queues.SMALL_BUFFER_SIZE bounded queue 임
    val sink = Sinks.many().multicast().directBestEffort<String>()
    val flux = sink.asFlux()

    sink.tryEmitNext("Hello")
    sink.tryEmitNext("How are you?")
    sink.tryEmitNext("Where are you from?")

    Thread.sleep(1000)

    // warmup : foo 는 위 3 메세지 받음. bar, sam 은 안받음
    flux.subscribe(Util.newSubscriber("foo"))
    flux.subscribe(Util.newSubscriber("bar"))
    flux.subscribe(Util.newSubscriber("sam"))

    sink.tryEmitNext("Late Message")
}