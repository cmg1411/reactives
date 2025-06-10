package com.kmg.udemyreactor.sec12

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Sinks
import java.time.Duration

private val log = LoggerFactory.getLogger("multicast")

fun main() {
//    demo8()
//    demo9()
    demo10()

    Thread.sleep(10000)
}

// onBackpressureBuffer 는 문제가 있다.
// 여러 구독자가 존재할 때, 각 큐의 메세지가 모든 구독자에게 전달되어야 다음 메세지로 넘어가기 때문에,
// 모든 구독자가 제일 느린 구독자의 속도로 고정된다.
// 큐의 크기를 넘어가기 시작하면, 빠른 구독자는 잃지 않아도 될 메세지를 잃게된다.
// 단순히 큐의 크기를 늘리는건 해결책이 아니다. 빠른 구독자가 빨리 받을 수 있길 원한다.
fun demo8() {
    System.setProperty("reactor.bufferSize.small", "16")

    val sink = Sinks.many().multicast().onBackpressureBuffer<String>()
    val flux = sink.asFlux()

    flux.subscribe(Util.newSubscriber("foo"))
    flux.delayElements(Duration.ofMillis(200)).subscribe(Util.newSubscriber("bar"))

    for (i in 1..100) {
        val result = sink.tryEmitNext("Hello $i")
        log.info("emit item : $i, result: $result")
    }
}

// directBestEffort
// 1. 빠른 구독자를 우선적으로 처리한다.
// 2. Warmup 과정이 없다. 뒤에 합류한 구독자는 이전에 메세지를 아예 받을 수 없다.
// 3. 느린 구독자에게는 데이터가 보이지 않을 수 있다.
// 4. 느린 구독자는 parallel 스레드에서 실행된다(왜지 ? 좀 더 볼것)
fun demo9() {
    System.setProperty("reactor.bufferSize.small", "16")

    val sink = Sinks.many().multicast().directBestEffort<String>()
    val flux = sink.asFlux()

    flux.subscribe(Util.newSubscriber("foo"))
    flux.delayElements(Duration.ofMillis(20)).subscribe(Util.newSubscriber("bar"))

    for (i in 1..100) {
        val result = sink.tryEmitNext("Hello $i")
        log.info("emit item : $i, result: $result")
    }

    Thread.sleep(1000)
    flux.subscribe(Util.newSubscriber("foo2"))

}

// 느린 구독자의 메세지가 유실되는게 싫다면, 느린 구독자의 앞에 버퍼(onBackpressureBuffer) 를 붙여서 일단 버퍼에 저장하고 나중에 받을 수 있다.
fun demo10() {
    System.setProperty("reactor.bufferSize.small", "16")

    val sink = Sinks.many().multicast().directBestEffort<String>()
    val flux = sink.asFlux()

    flux.subscribe(Util.newSubscriber("foo"))
    flux.onBackpressureBuffer().delayElements(Duration.ofMillis(200)).subscribe(Util.newSubscriber("bar"))

    for (i in 1..100) {
        val result = sink.tryEmitNext("Hello $i")
        log.info("emit item : $i, result: $result")
    }
}