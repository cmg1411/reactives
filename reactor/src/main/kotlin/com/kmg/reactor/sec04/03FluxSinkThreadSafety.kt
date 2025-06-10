package com.kmg.udemyreactor.sec04

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.util.function.Consumer

private val log = LoggerFactory.getLogger("FluxThreadSafety")

// FluxSink 는 thread-safe 하다.
// 병렬로 여러곳에서 sink.next() 를 호출해도 안전하다.
fun main() {
    val list = mutableListOf<String>()
//    threadUnsafeDemo(list) // 10000 보자 작음
    threadSafeDemo(list) // 10000
}

fun threadUnsafeDemo(list: MutableList<String>) {
    val runnable = {
        for (i in 1..1000) {
            list.add(i.toString())
        }
    }

    for (i in 1..10) {
        Thread.ofPlatform()
        Thread.ofPlatform().start(runnable)
    }

    Thread.sleep(3000)
    log.info("list size: ${list.size}")
}

fun threadSafeDemo(list: MutableList<String>) {
    val fluxSink = UserNameCreator2()
    val flux = Flux.create(fluxSink)
    flux.subscribe { list.add(it) }

    val runnable = {
        for (i in 1..1000) {
            fluxSink.generateUserName()
        }
    }

    for (i in 1..10) {
        Thread.ofPlatform().start(runnable)
    }

    Thread.sleep(3000)
    log.info("list size: ${list.size}")
}

class UserNameCreator2 : Consumer<FluxSink<String>> {

    private lateinit var sink: FluxSink<String>

    override fun accept(sink: FluxSink<String>) {
        this.sink = sink
    }

    fun generateUserName() {
        val userName = Util.faker.name().firstName()
        sink.next(userName)
    }
}