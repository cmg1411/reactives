package com.kmg.toby

import mu.KotlinLogging
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

fun main() {
    demonThread() // main 스레드가 종료되며 interval 이 찍히지 않음
//    userThread() // main 스레드가 끝나도 Hello 가 찍힘
    println("EXIT")
}

/**
 * interval 이 찍히지 않는 이유로
 * '부모 스레드 main 이 죽어서' 는 반쪽짜리 이유.
 * 데몬 스레드이기 때문에가 나머지 반쪽.
 */
fun demonThread() {
    Flux.interval(Duration.ofMillis(1000))
        .subscribe { log.info { it } }
}

fun userThread() {
    val es = Executors.newSingleThreadExecutor()

    es.execute {
        TimeUnit.SECONDS.sleep(3)
        log.info { "Hello" }
    }

    es.shutdown()
}