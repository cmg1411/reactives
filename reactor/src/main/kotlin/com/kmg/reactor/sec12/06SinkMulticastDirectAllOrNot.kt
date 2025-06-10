package com.kmg.udemyreactor.sec12

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Sinks
import java.time.Duration

private val log = LoggerFactory.getLogger("multicast")

// directAllOrNothing
// 메세지에 대해 메세지를 발행하되, 모든 구독자에게 줄 수 있으면 모두 주고, 하나라도 받을 수 없다면 모든 구독자에게 주지 않음.
// 아래 예는, 메세지를 10ms 마다 발행하고, foo 는 10ms 마다 메세지를 받고, bar 는 100ms 마다 메세지를 받는다.
// 그러면 100 ms 타이밍 마다의 메세지는 둘 다 받을 수 있으므로 받고, 나머지는 받을 수 없다. (이론상 10개의 메세지를 받게 하는 코드)
fun main() {
    System.setProperty("reactor.bufferSize.small", "16")

    val sink = Sinks.many().multicast().directAllOrNothing<String>()
    val flux = sink.asFlux()

    flux.delayElements(Duration.ofMillis(10)).subscribe(Util.newSubscriber("foo"))
    flux.delayElements(Duration.ofMillis(100)).subscribe(Util.newSubscriber("bar"))

    for (i in 1..100) {
        Thread.sleep(10)
        val result = sink.tryEmitNext("Hello $i")
        log.info("emit item : $i, result: $result")
    }
}