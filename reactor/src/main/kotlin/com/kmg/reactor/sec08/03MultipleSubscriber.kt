package com.kmg.udemyreactor.sec08

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

private val log = LoggerFactory.getLogger("backPressure")

// subscriber 가 여러개더라도, 각자 cold pub 으로 동작하기에, limitRate 걸린건 따로 잘 동작한다.
// 아래 예는 하나의 sub 은 느려서 limitRate 걸은 상황, 나머지는 정상이라 limitRate 안건상황이다.
fun main() {
    val producer = Flux.generate(
        { 1 },
        { state, sink ->
            log.info("generating: $state")
            sink.next(state)
            return@generate state + 1
        },
    ).subscribeOn(Schedulers.parallel())

    // 느린 sub. limitRate
    producer
        .limitRate(5)
        .publishOn(Schedulers.boundedElastic())
        .map(::timeConsumingTask)
        .subscribe(Util.newSubscriber())

    // 정상 sub
    producer
        .take(100)
        .publishOn(Schedulers.boundedElastic())
        .subscribe(Util.newSubscriber())

    Thread.sleep(30000)
}
