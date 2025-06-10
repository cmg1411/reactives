package com.kmg.udemyreactor.sec03

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

fun main() {
    val list = listOf(1, 2, 3, 4, 5)
    val stream = list.stream()

    // 스트림을 Flux 로 방출할 수 있다.
    // 하지만 이렇게 사용하면 안된다.
    Flux.fromStream(stream).subscribe(Util.newSubscriber())

    // java stream 은 일회용이다.
//    stream.forEach(::println)
//    stream.forEach(::println)

    // 따라서 stream 을 인자로 받는 fromStream 을 사용한 다음과 같은 코드도 안된다.
//    Flux.fromStream(stream).subscribe(Util.newSubscriber())ㄴㄴㄴ
//    Flux.fromStream(stream).subscribe(Util.newSubscriber())

    // 하나의 스트림을 재사용하여 여러 subscriber 를 붙이고 싶다면
    // 1. streamSupplier 를 인자로 받는 fromStream 을 사용한다.
    // 2. 매번 stream 을 생성하는 람다를 넘겨준다.
    Flux.fromStream { list.stream() }.subscribe(Util.newSubscriber("stream1"))
    Flux.fromStream { list.stream() }.subscribe(Util.newSubscriber("stream1"))
}