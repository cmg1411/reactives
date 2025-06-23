package com.kmg.reactor.sec09

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux

private val log = LoggerFactory.getLogger("")

// 여러 프로듀서를 묶을 수 있다.
// 프로듀서들에서 방출되는 이벤트는 순서가 보장되지 않는다.
// take() 처럼 하류에서 cancel 신호가 오면 모든 프로듀서가 cancel 되는듯?
fun main() {
    Flux.merge(producer1(), producer2(), producer3())
//        .take(2)
        .subscribe(Util.newSubscriber("mergeDemo"))

    producer1().mergeWith(producer2())
        .mergeWith(producer3())
//        .subscribe(Util.newSubscriber("mergeDemo"))

    Thread.sleep(3000)
}
