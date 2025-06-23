package com.kmg.reactor.sec10

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux
import java.time.Duration

// buffer
// 버퍼링은 이벤트를 모아서 한 번에 처리하는 방식
// 이 연산자는 정해진 만큼 모아서 Flux<List<T>> 로 반환
// ex) 빠르게 발생하는 스트림 이벤트를 디비에 하나씩 저장하면 효율이 안좋음. 버퍼에 모았다가 한번에 저장
fun main() {
    demo3()

    Thread.sleep(60000)
}

private fun eventStream(): Flux<String> {
    return Flux.interval(Duration.ofMillis(200))
        .take(110)
        .map { "Event $it" }
}

private fun demo1() {
    eventStream()
        .buffer()
        .subscribe(Util.newSubscriber())
}

private fun demo2() {
    eventStream()
        .buffer(3)
        .subscribe(Util.newSubscriber())
}

private fun demo3() {
    eventStream()
        .buffer(Duration.ofMillis(500))
        .subscribe(Util.newSubscriber())
}

private fun demo4() {
    eventStream()
        .bufferTimeout(3, Duration.ofMillis(500))
        .subscribe(Util.newSubscriber())
}
