package com.kmg.udemyreactor.sec12

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Sinks
import java.time.Duration

// n 개의 구독자가 붙을 수 있으며, 붙는 타이밍에 관계없이 기존의 메세지를 읽을 수 있다.
// 1. replay().all() 모든 메세지를 저장하고 구독자에게 준다.
// 2. replay().latest() 가장 최근의 메세지만 저장하고 구독자에게 준다.
// 3. replay().limit(10) 가장 최근의 10개의 메세지만 저장하고 구독자에게 준다.
// 4. replay().limit(Duration.ofSeconds(10)) 가장 최근의 10초 동안의 메세지만 저장하고 구독자에게 준다.
fun main() {

//    val sink = Sinks.many().replay().all<String>()
//    val sink = Sinks.many().replay().latest<String>()
//    val sink = Sinks.many().replay().limit<String>(10)
    val sink = Sinks.many().replay().limit<String>(Duration.ofSeconds(10))
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