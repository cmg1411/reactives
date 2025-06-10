package com.kmg.udemyreactor.sec08

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

private val log = LoggerFactory.getLogger("backPressure")

// Flux.generate 는 emit 루프를 자동으로 관리한다. 따라서 backPressure 도 자동으로 관리된다.
// 반면 Flux.create 는 루프도 개발자가 관리한다. 따라서 BackPressure 가 따로 관리되지 않아서, 적절한 방법으로 직접 처리해야한다.
// 여러 전략이 있다. (Ex. buffer strategy, error strategy..)
fun main() {
    val producer = Flux.create { sink ->
        for (i in 1..50000) {
            if (sink.isCancelled) break
            log.info("generating: $i")
            sink.next(i)
            Thread.sleep(1)
        }
        sink.complete()
    }

    producer
//        .onBackpressureBuffer() // unbounded queue(size = MAX_VALUE)
//        .onBackpressureBuffer(4) // bounded queue(size = 인자)
//        .onBackpressureError()
        .onBackpressureLatest()
//        .onBackpressureDrop()
        .limitRate(15)
        .publishOn(Schedulers.boundedElastic())
        .map(::timeConsumingTask)
        .subscribe(Util.newSubscriber())

    Thread.sleep(30000)
}
