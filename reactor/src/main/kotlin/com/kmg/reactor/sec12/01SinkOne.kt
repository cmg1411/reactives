package com.kmg.udemyreactor.sec12

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Sinks

private val log = LoggerFactory.getLogger("backPressure")

// sink -> 프로그래밍 방법으로 이벤트를 방출시키는 방법
// 기존에 Flux.create, generate 등으로 방출했지만, sink 를 통한 방출이 더 최신의 방법.
// Reactive Streams에서 발생하는 signal을 프로그래밍적으로 push할 수 있는 기능을 가지고 있는 Publisher의 일종
// Reactor에서 프로그래밍 방식으로 signal을 전송하는 가장 일반적인 방법은 generate() Operator나 create() Operator 등을 사용하는 것인데, 이는 싱글스레드 기반에서 signal을 전송합니다. 반면, Sinks는 멀티스레드 방식으로 signal을 전송해도 스레드 안전성을 보장하기 때문에 예기치 않은 동작으로 이어지는 것을 방지

// 1. one : Mono. N 개의 subscriber.
// 2. many - unicast : Flux. 1개의 subscriber. 방출된 메세지가 메모리에 저장되기에 subscriber 가 늦게 들어와도 이전에 메세지 받을 수 있음.
// 3. many - multicast : Flux. N개의 subscriber. 늦게 들어온 subscriber 는 메세지를 받을 수 없음. (unicast 와 다르게 메모리에 저장되지 않음)
// 4. many - replay : Flux. N개의 subscriber. 방출된 메세지가 메모리에 저장되기에 subscriber 가 늦게 들어와도 이전에 메세지 받을 수 있음.
fun main() {
    demo3()
}

fun demo1() {
    val sink = Sinks.one<String>()
    val mono = sink.asMono()
    mono.subscribe(Util.newSubscriber())

    sink.tryEmitValue("Hello")
//    sink.tryEmitEmpty()
//    sink.tryEmitError(RuntimeException("Oops"))
}

// multiple subscriber - cold pub 이라 둘 다 받음.
fun demo2() {
    val sink = Sinks.one<String>()
    val mono = sink.asMono()
    mono.subscribe(Util.newSubscriber("foo"))
    mono.subscribe(Util.newSubscriber("bar"))

    sink.tryEmitValue("Hello")
}

// emitValue = tryEmitValue + errorHandle
fun demo3() {
    val sink = Sinks.one<String>()
    val mono = sink.asMono()
    mono.subscribe(Util.newSubscriber())

    sink.emitValue("Hello") { signalType, emitResult ->
        log.info("HELLO")
        log.info(signalType.name)
        log.info(emitResult.name)
        return@emitValue false
    }

    // 위의 Mono 가 구독완료되어 complete 이후인데 한번더 방출한 에러상황
    sink.emitValue("Hi") { signalType, emitResult ->
        log.info("HI")
        log.info(signalType.name)
        log.info(emitResult.name)
        return@emitValue false
    }
}