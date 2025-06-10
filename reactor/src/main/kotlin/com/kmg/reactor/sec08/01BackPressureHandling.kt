package com.kmg.udemyreactor.sec08

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.util.logging.Level

private val log = LoggerFactory.getLogger("backPressure")

// 무한으로 이벤트를 발산하는 Flux producer 가 있고,
// publishOn 기점으로 아래에 timeConsumingTask 가 존재한다.
// 빠른 publisher, 느린 subscriber 의 상황이며, 이 경우 배압이 자동으로 동작하여 producer 측의 oom 등을 방지한다.
// producer 는 Queues 라는 곳에 기본적으로 데이터를 저장하고 꽉 찰 경우 producing 을 중단한다.
// 큐에 75% 여유가 존재하면 producing 을 재개한다.
fun main() {
    // 해당 설정으로 큐의 크기를 변경할 수 있다.
    System.setProperty("reactor.bufferSize.small", "16")

    val producer2 = Flux.generate(
        { 1 },
        { state, sink ->
            log.info("generating: $state")
            sink.next(state)
            return@generate state + 1
        },
    )

//    val producer = Flux.interval(Duration.ofMillis(1))

    producer2
        .map { it.toInt() }
        .log(null, Level.WARNING)
        .publishOn(Schedulers.boundedElastic())
        .map(::timeConsumingTask)
        .subscribe(Util.newSubscriber())

    Thread.sleep(300000)
}

fun timeConsumingTask(i: Int): Int {
    Thread.sleep(1500)
    return i
}
