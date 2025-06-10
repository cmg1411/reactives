package com.kmg.udemyreactor.sec07

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

private val log = LoggerFactory.getLogger("logger")

// publishOn 은 구독자의 동작이 어디서 실행 될 것인가에 관련있다.
// downStream 동작을 어느 스케줄러에서 실행할 지 결정한다.
// 즉, publisher 의 데이터 Emit 이후의 operator, subscriber 의 실행 위치를 제어할 수 있다.
// 아래의 예의 경우 first2 는 subscribe 실행 스레드에서,
// first1, Flux Emit 은 subscribeOn 에 의해 boundedElastic 에서 실행되고,
// 이후 doOnNext 와 subscriber 의 onNext, onComplete 는 publishOn 에 의해 parallel 에서 실행된다.,
fun main() {
    val flux = Flux.create { sink ->
        for (i in 1..100000) {
            log.info("generating: $i")
            sink.next(i)
        }
        sink.complete()
    }
        .publishOn(Schedulers.parallel())
        .doOnNext { log.info("doOnNext: $it") }
        .doFirst { log.info("first1") }
        .subscribeOn(Schedulers.boundedElastic())
        .doFirst { log.info("first2") }

    Thread.ofPlatform().start { flux.subscribe(Util.newSubscriber("sub1")) }
    Thread.ofPlatform().start { flux.subscribe(Util.newSubscriber("sub1")) }

    Thread.sleep(3000)
}