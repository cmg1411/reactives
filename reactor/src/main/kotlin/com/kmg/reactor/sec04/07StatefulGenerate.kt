package com.kmg.udemyreactor.sec04

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux
import reactor.core.publisher.SynchronousSink

fun main() {
    // 상태를 사용하고싶다면, stateSupplier 인자를 가진 generate 메서드를 사용해야 한다.
    // stateSupplier 는 첫 방출 전 한번만 실행된다.
    // stateConsumer 는 state 객체에 대한 마지막 동작 (ex. close..) 을 수행할 수 있다. 이 역시 한번만 실행됨.
    Flux.generate(
        { 0 },
        { state: Int, sink: SynchronousSink<String> ->
            var counter = state
            val country = Util.faker.country().name()
            sink.next(country)
            counter++
            // counter 라는 상태를 사용하고 싶다.
            if (counter == 10 || country.lowercase() == "canada") {
                sink.complete()
            }
            return@generate counter

        },
        { state -> println("state: $state") }
    ).subscribe(Util.newSubscriber())
}