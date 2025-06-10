package com.kmg.udemyreactor.sec08

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

private val log = LoggerFactory.getLogger("backPressure")

// limitRate 를 사용하면, backPressure 를 적용할 기준을 정할 수 있다.
fun main() {
    val producer = Flux.generate(
        { 1 },
        { state, sink ->
            log.info("generating: $state")
            sink.next(state)
            return@generate state + 1
        },
    ).subscribeOn(Schedulers.parallel())

    producer
        .limitRate(5)
        .publishOn(Schedulers.boundedElastic())
        .map(::timeConsumingTask)
        .subscribe(Util.newSubscriber())

    Thread.sleep(30000)
}
