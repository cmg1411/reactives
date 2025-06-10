package com.kmg.udemyreactor.sec05

import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux

private val log = LoggerFactory.getLogger("subscribe")

// Util.newSubscriber() 에서 사용하는 DefaultSubscriber 의 구현은
// doOnNext, doOnComplete, doOnError 를 사용하여 대체가 가능하다.
// 이 때 subscribe() 기본 메서드를 사용하고, 이는 LambdaSubscriber 를 사용한다.
// 상황에 따라 맞춰서 서로 바꿔쓸 수 있겠다.
fun main() {
    Flux.range(1, 10)
        .doOnNext { log.info("received : $it") }
        .doOnComplete { log.info("completed!") }
        .doOnError { log.info("error : $it") }
        .subscribe()
}