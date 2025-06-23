package com.kmg.reactor.sec09

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux

private val log = LoggerFactory.getLogger("")

// startWith 은 캐시에서 사용될 수 있다.
// 아래 예는 startWith(redis) 로 캐시에서 먼저 조회 후 데이터를 generate 한다.
// 따라서 startWith 가 없는 경우보다 훨씰 빨리 끝남.
fun main() {
    nameGenerator()
        .take(2)
        .subscribe(Util.newSubscriber())

    nameGenerator()
        .take(2)
        .subscribe(Util.newSubscriber())

    nameGenerator()
        .take(3)
        .subscribe(Util.newSubscriber())
}

val redisMock = mutableListOf<String>()

fun nameGenerator(): Flux<String> {
    return Flux.generate<String> {
        log.info("Generating name")
        Thread.sleep(1000)
        val name = Util.faker.name().firstName()
        redisMock.add(name)
        it.next(name)
    }
        .startWith(redisMock)
}