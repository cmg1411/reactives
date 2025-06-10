package com.kmg.udemyreactor.sec07

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime

private val log = LoggerFactory.getLogger("parallel")

fun asmain() {
    Flux.range(1, 10000)
//        .publishOn(Schedulers.parallel())
//        .publishOn(Schedulers.boundedElastic())
        .parallel()
        .runOn(Schedulers.parallel())
        .map {
            log.info("time consuming task: $it")
            Thread.sleep(1000)
            it * 2
        }
        .subscribe(Util.newSubscriber())

    Thread.sleep(5000)
}

/**
 * parallelFlux 는 CPU 개수에 맞춰서 라운드로빈으로 병렬화. 따라서 모든 작업이 CPU
 * CPU bounded 작업에 유용. I/O 는 어짜피 내 CPU 가 일하는게 아니라 상관 없음.
 *
 * ParallelFlux 는 기본적으로 프로세서 개수만큼 rail 을 나눔
 * 따라서 CPU 바운디드 작업에 쓰이는 것.
 *
 * 블로킹 I/O 를 실행할 때에는 flatMap + subscribeOn 이 낫다.
 * paralleism 을 조정할 순 있겠지만..... I/O 실행은 굳이 레일을 나누고 하는 작업이 필요치 않다.
 *
 */
fun main() {
    val rxGroceries = RxGroceries()

//    Flux.just("milk", "egg", "bread")
//        .subscribeOn(Schedulers.boundedElastic())
//        .map { rxGroceries.doPurchase(it, 1) }
//        .reduce { a, b -> a + b}
//        .subscribe(Util.newSubscriber())

//    Flux.just("milk", "egg", "bread")
//        .flatMap { rxGroceries.purchase(it, 1)
//            .subscribeOn(Schedulers.boundedElastic())
//        }
//        .onErrorReturn(-1)
//        .reduce { a, b -> a + b }
//        .subscribe(Util.newSubscriber())
//
//    Flux.just("milk", "egg", "bread")
//        .parallel()
//        .runOn(Schedulers.boundedElastic())
//        .flatMap { rxGroceries.purchase(it, 1) }
//        .reduce { a, b -> a + b }
//        .subscribe(Util.newSubscriber())


    println(LocalDateTime.now())
//    val a = Mono.just(LocalDateTime.now())
    val a = Mono.defer { Mono.just(LocalDateTime.now()) }
    Thread.sleep(4000)
    a.subscribe(System.out::println)
    println(LocalDateTime.now())
    Thread.sleep(4000)
}

class RxGroceries {
    fun purchase(productName: String, quantity: Int): Mono<Int> {
        return Mono.fromCallable { doPurchase(productName, quantity) }
    }

    fun doPurchase(productName: String, quantity: Int): Int {
        log.info("purchasing $productName quantity $quantity")

        val price = quantity * 1000
        Thread.sleep(1000) // Simulate a time-consuming task

        log.info("Done purchasing $productName quantity $quantity")
        return price
    }
}
