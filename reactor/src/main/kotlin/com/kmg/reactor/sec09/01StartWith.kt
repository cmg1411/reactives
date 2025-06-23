package com.kmg.reactor.sec09

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import java.time.Duration

private val log = LoggerFactory.getLogger("")

// startWith 는 publisher 의 시작 전에 먼저 emit 할 producer 를 지정한다.
// demo2 - list 도 지정이 가능하다. list 에서 하나씩 빼서 준다.
// demo3 - 다른 Flux 도 가능.
// demo4 - 여러 startWith 가 잇으면 아래 정의된게 먼저 실행된다.
fun main() {
    demo4()
    Thread.sleep(3000)
}

fun demo1() {
    producer1()
        .startWith(-1, 0)
        .subscribe(Util.newSubscriber("demo1"))

    Thread.sleep(3000)
}

fun demo2() {
    producer1()
        .startWith(listOf(-2, -1, 0))
        .subscribe(Util.newSubscriber("demo1"))

    Thread.sleep(3000)
}

fun demo3() {
    producer1()
        .startWith(producer2())
        .subscribe(Util.newSubscriber("demo1"))

    Thread.sleep(3000)
}

fun demo4() {
    producer1()
        .startWith(0)
        .startWith(producer2())
        .startWith(1000)
        .subscribe(Util.newSubscriber("demo1"))

    Thread.sleep(3000)
}


fun producer1(): Flux<Int> {
    return Flux.just(1, 2, 3)
        .transform(Util.fluxLogger<Int>("producer1"))
        .delayElements(Duration.ofMillis(10))
}

fun producer2(): Flux<Int> {
    return Flux.just(51, 52, 53)
        .transform(Util.fluxLogger<Int>("producer2"))
        .delayElements(Duration.ofMillis(10))
}