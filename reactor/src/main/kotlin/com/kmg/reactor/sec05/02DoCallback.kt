package com.kmg.udemyreactor.sec05

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux

private val log = LoggerFactory.getLogger("doCallback")

// doOn~ 콜백은 Mono, Flux 상 특정 타이밍에 실행할 수 있는 콜백 오퍼레이터이다.
// generate, create, handle 등 fluxSink 나 synchronousSink 를 가지는 메서드는 Produce 를 제어하는 목적으로 사용하는 반면,
// do Callback 의 doOnNext 는 item 을 수정할 수 있지만, publish 를 제어하지 않는다.
// doOnNext 빼고는 값을 변환하지는 않음.
// doOnNext, doOnComplete, doOnSubscribe, doOnDiscard 들은 Producer 가 주체이므로 위의 operation 이 먼저 실행
// doFirst, doOnRequest, doOnCancel, doOnError 들은 Subscriber 가 주체이므로 아래의 operation 이 먼저 실행
// doOnDiscard : producer 는 데이터를 생성했으나, take 등의 이유로 버려지는 데이터가 있다면 doOnDiscard 를 사용할 수 있다.
fun main() {
    Flux.create<Int> { fluxSink ->
        log.info("producer begins")
        for (i in 0..3) fluxSink.next(i)
        fluxSink.complete()
//         fluxSink.error(RuntimeException("oops"))
        log.info("producer ends")
    }
    .doOnComplete { log.info("doOnComplete-1") }
    .doFirst { log.info("doFirst-1") }
    .doOnNext { item -> log.info("doOnNext-1: $item") }
    .doOnSubscribe { subscription -> log.info("doOnSubscribe-1: $subscription") }
    .doOnRequest { request -> log.info("doOnRequest-1: $request") }
    .doOnError { error -> log.info("doOnError-1: $error") }
    .doOnTerminate { log.info("doOnTerminate-1") } // complete or error case
    .doOnCancel { log.info("doOnCancel-1") }
    .doOnDiscard(Object::class.java) { obj -> log.info("doOnDiscard-1: $obj") }
    .doFinally { signal -> log.info("doFinally-1: $signal") } // finally irrespective of the reason
     .take(2)
    .doOnComplete { log.info("doOnComplete-2") }
    .doFirst { log.info("doFirst-2") }
    .doOnNext { item -> log.info("doOnNext-2: $item") }
    .doOnSubscribe { subscription -> log.info("doOnSubscribe-2: $subscription") }
    .doOnRequest { request -> log.info("doOnRequest-2: $request") }
    .doOnError { error -> log.info("doOnError-2: $error") }
    .doOnTerminate { log.info("doOnTerminate-2") } // complete or error case
    .doOnCancel { log.info("doOnCancel-2") }
    .doOnDiscard(Object::class.java) { obj -> log.info("doOnDiscard-2: $obj") }
    .doFinally { signal -> log.info("doFinally-2: $signal") } // finally irrespective of the reason
    //.take(4)
    .subscribe(Util.newSubscriber("subscriber"))
}