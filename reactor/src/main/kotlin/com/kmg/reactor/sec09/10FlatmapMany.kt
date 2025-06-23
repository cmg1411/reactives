package com.kmg.reactor.sec09

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

// flatMapMany 은 Mono 의 메서드다.
// 단건(mono) 의 값을 사용하여 결과가 여러건(flux)인 프로듀서를 생성 및 실행할 때 사용한다.
fun main() {
    val number = Mono.just(10)
    val range = number.flatMapMany { Flux.range(1, it) }
    range.subscribe(Util.newSubscriber())
}