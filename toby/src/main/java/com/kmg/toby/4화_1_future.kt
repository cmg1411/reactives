package com.kmg.toby
import mu.KotlinLogging
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

private val log = KotlinLogging.logger {}

/**
 * ExecutorService
 * execute : 리턴값이 없다. 단순 비동기 실행.
 * submit : 리턴값이 있다. 비동기 실행 후 결과를 Future 타입으로 받을 수 있다. Future 의 get() 은 Blocking Method.
 */
fun main() {
    val es = Executors.newCachedThreadPool() // 스레드를 캐싱하는 풀을 생성.

//    execute(es) // EXIT 이 먼저.
    submit(es) // EXIT 이 마지막에.

    log.info("EXIT")
}

fun execute(es: ExecutorService) {
    es.execute {
        Thread.sleep(2000)
        log.info("Hello")
    }
}

fun submit(es: ExecutorService) {
    val future: Future<String> = es.submit<String> {
        Thread.sleep(2000)
        log.info("World")
        "Hello"
    }

    println(future.get()) // Future 의 get() 은 Blocking Method
}
