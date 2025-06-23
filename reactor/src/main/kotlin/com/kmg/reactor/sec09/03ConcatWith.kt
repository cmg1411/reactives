package com.kmg.reactor.sec09

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux
import java.time.Duration

// concatWith 은 startWith 과 반대다.
// producer 의 emit 후에 concatWith 에 선언된 publisher 를 emit 한다.
// concatWith 은 위에서 아래로 순서대로 실행된다.
fun main() {
    concatWithDemo2()

    Thread.sleep(3000)
}

fun concatWithDemo() {
    producer1()
        .concatWith(producer2())
        .subscribe(Util.newSubscriber("concatWithDemo"))

    Thread.sleep(3000)
}

fun concatWithDemo2() {
    producer1()
        .concatWith(producer2())
        .concatWith(producer3())
        .subscribe(Util.newSubscriber("concatWithDemo"))

    Thread.sleep(3000)
}

fun producer3(): Flux<Int> {
    return Flux.just(4, 5, 6)
        .transform(Util.fluxLogger<Int>("producer3"))
        .delayElements(Duration.ofMillis(10))
}