package com.kmg.udemyreactor.sec12

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Sinks

private val log = LoggerFactory.getLogger("unicast")

// Sinks.many().unicast() 는 1개의 subscriber 만 받을 수 있다.
// 방출된 메세지가 메모리에 저장되기에 subscriber 가 늦게 들어와도 이전에 메세지 받을 수 있음.
fun main() {
    demo4()
}

fun demo4() {
    // onBackpressureBuffer 는 기본적으로 unbounded queue
    val sink = Sinks.many().unicast().onBackpressureBuffer<String>()
    val flux = sink.asFlux()

    sink.tryEmitNext("Hello")
    sink.tryEmitNext("Nice to meet you")
    sink.tryEmitNext("Where are you from?")

    flux.subscribe(Util.newSubscriber())
}

// error. unicast 는 1개의 subscriber 만 받을 수 있다.
fun demo5() {
    val sink = Sinks.many().unicast().onBackpressureBuffer<String>()
    val flux = sink.asFlux()

    sink.tryEmitNext("Hello")
    sink.tryEmitNext("Nice to meet you")
    sink.tryEmitNext("Where are you from?")

    flux.subscribe(Util.newSubscriber("foo"))
    flux.subscribe(Util.newSubscriber("bar"))
}