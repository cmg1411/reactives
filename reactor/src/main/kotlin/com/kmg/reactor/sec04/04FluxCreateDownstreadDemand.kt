package com.kmg.udemyreactor.sec04

import com.kmg.udemyreactor.common.Util
import com.kmg.udemyreactor.sec01.subscriber.SubscriberImpl
import org.reactivestreams.Subscriber
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux

private val log = LoggerFactory.getLogger("0304")

fun main() {
    // request() 를 제어하기 위해 SubscriberImpl 사용
    val subscriber = SubscriberImpl()

//    fluxSinkProduceEarly(subscriber)
    fluxSinkProduceOnDemand(subscriber)


    subscriber.subscription.request(2)
    Thread.sleep(3000)
    subscriber.subscription.request(2)
    Thread.sleep(3000)

    subscriber.subscription.cancel()

    subscriber.subscription.request(2)
    Thread.sleep(3000)
}

fun fluxSinkProduceEarly(subscriber: Subscriber<String>) {
    return Flux.create { fluxSink ->
        for (i in 1..10) {
            val name = Util.faker.name().firstName()
            log.info("generated : $name")
            fluxSink.next(name)
        }

        fluxSink.complete()
    }.subscribe(subscriber)
    // subscribe 등록만 해도 (request 하지 않아도) FluxSink 로직이 돈다.
    // lazy 하게 돌지 않는다는 것.
    // 만들어진 Flux 들은 큐에 저장되어 있다가 request 시 request 개수만큼 방출한다.
    // 이 큐는 크기가 MAX 인 큐이다. 메모리 관리에 유의해야한다.
}

fun fluxSinkProduceOnDemand(subscriber: Subscriber<String>) {
    // fluxSink.onRequest() 를 사용하면 FluxSink 로직을
    // request() 가 호출되었을때 lazy 하게 실행할 수 있다.
    return Flux.create { fluxSink ->
        if(fluxSink.isCancelled) {
            log.info("cancelled")
            return@create
        }

        fluxSink.onRequest { request ->
            for (i in 1..request) {
                val name = Util.faker.name().firstName()
                log.info("generated : $name")
                fluxSink.next(name)
            }
        }
    }.subscribe(subscriber)
}