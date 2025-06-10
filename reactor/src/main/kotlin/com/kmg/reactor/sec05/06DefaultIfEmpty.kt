package com.kmg.udemyreactor.sec05

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

// defaultIfEmpty : Flux.empty() 또는 Mono.empty() 를 방출하는 경우 디폴트 값 지정 가능.
fun main() {
    // 모두 filter 에 걸려서 Flux.empty()
    // defaultIfEmpty 가 호출되어 50 이 방출된다.
    Flux.range(1, 10)
        .filter { it > 10 }
        .defaultIfEmpty(50)
        .subscribe(Util.newSubscriber())

    // 10 개 중 9 개가 filter 에 걸려서 방출된다.
    // 10 은 방출되므로 defaultIfEmpty 가 호출되지 않는다.
    Flux.range(1, 10)
        .filter { it > 9 }
        .defaultIfEmpty(50)
        .subscribe(Util.newSubscriber())
}