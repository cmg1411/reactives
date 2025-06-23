package com.kmg.reactor.sec10

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

// window 는 buffer 와 다르게 정해진 크기만큼의 Flux 를 각각 생성한다.
// [리턴 비교] buffer : Flux<List<T>>, window: Flux<Flux<T>>
// ex) 로그를 일단위로 파일로 저장할 때. 일별로 다른 Flux 구독자를 가지는 것.
// 리턴이 Flux<Flux<T>> 이므로, 이후에 flatMap 이 왼만해선 뒤 따라온다.

// processingEvent 예시를 보면 별모양이 개행되는 시점이 각 Flux 의 끝이다.
fun main() {
    Flux.interval(Duration.ofMillis(200))
//        .window(5)
        .window(Duration.ofMillis(1800))
        .flatMap(::processingEvent)
//        .flatMap { flux -> flux.map { "Event $it" } }
        .subscribe { println(it) }

    Thread.sleep(60000)
}

private fun processingEvent(flux: Flux<Long>): Mono<Void> {
    return flux.doOnNext { print("*") }
        .doOnComplete(::println)
        .then()
}