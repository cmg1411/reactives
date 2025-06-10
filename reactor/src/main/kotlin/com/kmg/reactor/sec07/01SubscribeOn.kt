package com.kmg.udemyreactor.sec07

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

private val log = LoggerFactory.getLogger("subscribeOn")

// subscribeOn 은 upstream 에서 동작한다.
// 즉 처음 subscriber 가 붙어서 stream 을 올라갈 때 subscribeOn 을 만나면 그 때부터 모든 동작이
// scheduler 에서 동작한다.
// 따라서 아래도 처음 doFirst 의 first2 logging 이외애 모든 작업이 boudedElastic 에서 동작한다.
fun main() {
    val flux = Flux.create { sink ->
        for (i in 1..3) {
            log.info("generating: $i")
            sink.next(i)
        }
        sink.complete()
    }
        .doOnNext { log.info("doOnNext: $it") }
        .doFirst { log.info("first1") }
        .subscribeOn(Schedulers.boundedElastic())
        .doFirst { log.info("first2") }

    Thread.ofPlatform().start { flux.subscribe(Util.newSubscriber("sub1")) }
    Thread.ofPlatform().start { flux.subscribe(Util.newSubscriber("sub1")) }

    Thread.sleep(3000)
}
