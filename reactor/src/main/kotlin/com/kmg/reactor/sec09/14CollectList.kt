package com.kmg.reactor.sec09

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

// collectList 는 Flux<T> 의 모든 아이템을 수집하여 Mono<List<T>> 로 변환한다.
// blocking  아니다.
fun main() {
    Flux.range(1, 10)
        // Mono<List<Int>> 로 변환한다.
        .collectList()
        .subscribe(Util.newSubscriber())
}