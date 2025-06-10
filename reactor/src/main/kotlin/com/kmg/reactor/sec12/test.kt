package com.kmg.udemyreactor.sec12

import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

private val log = LoggerFactory.getLogger("")

val list = (1..10000).toList()
val pageSize = 10

//P137
fun main() {
//    allNumber(3)
    allNumberFlux(3)
        .doOnComplete { println("complete") }
        .subscribe(System.out::println)

    Thread.sleep(100000)
}

fun getNumbers(page: Int): List<Int> {
    log.info("page: $page")
    val allPages = list.size / pageSize
    if (page >= allPages) return emptyList()
    return list.subList(page * pageSize, page * pageSize + pageSize)
}

fun allNumber(initialPage: Int): Flux<Int> {
    return Flux.fromIterable(getNumbers(initialPage))
        .concatWith(Flux.defer {
            if (getNumbers(initialPage).isEmpty()) {
                Flux.empty()
            } else {
                allNumber(initialPage + 1) }
            })

}

fun getNumbersFlux(page: Int): Flux<Int> {
    return Flux.range(page * pageSize, pageSize)
}

fun allNumberFlux(initialPage: Int): Flux<Int> {
    return getNumbersFlux(initialPage)
        .concatWith(Flux.defer {
            if (getNumbers(initialPage).isEmpty()) {
                Flux.empty()
            } else {
                allNumberFlux(initialPage + 1) }
        })
}