package com.kmg.udemyreactor.sec02

import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

private val log = LoggerFactory.getLogger("MonoSubscribe")

fun main() {
    val mono = Mono.just("1")
//        .map { it / 0 }
        .map { it + "a" }

    // 이 함수를 포함한 몇몇 subscribe 함수에서는 LambdaMonoSubscriber 라는 구독자를 사용한다.
    // LambdaMonoSubscriber 는 onSubscribe() 에서 request 를 Long.MAX_VALUE 만큼 호출한다.
    // 따라서 여기서는 추가로 request 를 요청하지 않고 subscribe 만 해도 구독된다.
    // reactive stream 스펙상 onSubscribe() 에서 request 를 다 호출하라는 제한은 없다.
    // reactor 에서 onSubscribe() 에서 다 request 하는 구현을 하는 것 뿐.. 근데 그게 대부분인가 보다. 이렇게 구현해놓은것 보면.
    mono.subscribe(
        { i -> log.info("received: $i") },
        { e -> log.info("error occur zz: $e") },
        { log.info("completed!") },
        // subscriptionConsumer 를 인자로 받을 수 있다. 이 Consumer 는 인자로 subscription 이 들어오며, request, cancel 을 할 수 있다.
        // LambdaMonoSubscriber 의 onSubscribe() 동작상 Long.MAX_VALUE request 보다 먼저 이 subscriptionConsumer 를 실행한다.
//        { s -> log.info("subscription: $s"); s.request(30) }
    )
}