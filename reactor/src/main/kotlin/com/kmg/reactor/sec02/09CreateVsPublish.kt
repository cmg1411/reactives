package com.kmg.udemyreactor.sec02

import com.github.javafaker.Name
import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

private val log = LoggerFactory.getLogger("FromFuture")

fun main() {
    // 이 코드는 "getMono() called" 주석은 찍힌다.
    // subscriber 가 없어서 publish 는 되지 않는다.
    // 다만 메서드 실행은 되었고, Mono(Publisher) 자체는 만들어졌다.
    // 이 차이가 헷갈릴 수 있다.
    getMono()
}

fun getMono(): Mono<Name> {
    log.info("getMono() called")
    return Mono.fromSupplier {
        log.info("generating name")
        Thread.sleep(3000)
        Util.faker.name()
    }
}