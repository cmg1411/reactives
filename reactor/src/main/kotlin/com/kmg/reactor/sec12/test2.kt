package com.kmg.udemyreactor.sec12

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Operators
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Callable

private val log = LoggerFactory.getLogger("loggerName")

var cnt = 0

fun cpuTask(index: Int): Flux<Int> {
    cnt++
    return Flux.just(index)
        .delayElements(Duration.ofSeconds(if(cnt in 8..10) 10 else 0))
}

fun main() {
    // reactor buffer 보다 prefetch 가 크다면?
    // cold, hot 의 경우 여러 구독자가 있을때 백프레셔의 큐가 어떻게 되는가? 각각 생기는가? 하나 생기는가? 하나 생기면 데이터는 복사해서 주나?
    // 왜 256 ? spsc 알고리즘 큐랑 관련있나 ?
    Flux.range(1, 10)
        .map {
            log.info(it.toString())
            it
        }.delayElements(Duration.ofMillis(10))
        .subscribe(Util.newSubscriber())



//    val init = System.currentTimeMillis()
//    Flux.interval(Duration.ofMillis(7))
//        .timestamp()
//        .sample(Duration.ofSeconds(1))
//        .map { "${it.t1 - init}ms: ${it.t2}" }
////        .subscribe(System.out::println)
//
//    val names = Flux.just("Mary", "Patricia", "Linda", "Barbara", "Elizabeth", "Jennifer", "Maria", "Susan", "Margaret", "Dorothy")
//
//    val delay = Flux.just(0.1, 0.6, 0.9, 1.1, 3.3, 3.4, 3.5, 3.6, 4.4, 4.8)
//        .map { (it * 1000).toLong() }
//
//    val delayedNames = names.zipWith(delay) { name, delay ->
//        Flux.just(name)
//            .delayElements(Duration.ofMillis(delay))
//    }.flatMap { it }
//
//    delayedNames
//        .sample(Duration.ofSeconds(1)) // 마지막 데이터 방출함.
//        .subscribe { println("name: $it") }
//
//    delayedNames
//        .concatWith(Flux.empty<String>().delayElements(Duration.ofSeconds(1)))
//        .sample(Duration.ofSeconds(1))
//        .subscribe { println("name: $it") }

    Thread.sleep(55000)
}

var count = 0

fun getDB(index: Int): Flux<Int> {
    count++
    return Flux.just(1, 2, 3, 4, 5)
        .delayElements(Duration.ofSeconds(index.toLong()))
}