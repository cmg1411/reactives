package com.kmg.reactor.sec09

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

// concatWith 은 순차적으로 emit 되는 producer 중 error 가 있으면 그 위치에서 에러 발생하면서 멈추며 뒤는 실행되지 않음.
// concatDelayError 은 error 가 발생하더라도 바로 멈추지 않고 에러 정보를 전파시키면서 이후 producer 를 실행. 모두 실행 뒤 마지막에 에러 시그널 보냄.
fun main() {
    val producer = Flux.generate<Int> {
        it.error(RuntimeException("Error occurred"))
    }

    producer
        .concatWith(producer2())
        .concatWith(producer3())
//        .subscribe(Util.newSubscriber())


    Flux.concatDelayError(producer, producer2(), producer3())
        .subscribe(Util.newSubscriber())

    Thread.sleep(3000)
}