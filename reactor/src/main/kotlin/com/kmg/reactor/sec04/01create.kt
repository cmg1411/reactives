package com.kmg.udemyreactor.sec04

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.util.function.Consumer

// create 는 인지의 Consumer<FluxSink<T>> 를 subscribe 시 한번 실행한다.
// create 는 생산 루프를 모두 컨트롤한다.
// 매번 자동화된 방출을 실행하려면 generate 사용.
fun main() {
    val sinker = UserNameCreator()
    val flux = Flux.create(sinker)
    flux.subscribe(Util.newSubscriber())

    while (true){
        Thread.sleep(300)
        sinker.generateUserName()
    }
}

class UserNameCreator : Consumer<FluxSink<String>> {

    private lateinit var sink: FluxSink<String>

    override fun accept(sink: FluxSink<String>) {
        this.sink = sink
    }

    fun generateUserName() {
        val userName = Util.faker.name().firstName()
        if (userName.startsWith("A")) sink.complete()
        sink.next(userName)
    }
}
