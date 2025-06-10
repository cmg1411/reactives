package com.kmg.udemyreactor.sec03

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

fun main() {
    // interval 은 시간 단위마다 0 부터 시작하는 숫자를 방출한다.
    // 1초마다 0, 1, 2, 3, ...
    // interval 은 무한 스트림이다.
    // 필요시 complete 또는 cancel 해야함.
    // subscribe 를 하지 않으면 아무것도 방출하지 않는다.
    // interval 은 기본적으로 밀리세컨드 단위로 방출한다.

    Flux.interval(java.time.Duration.ofSeconds(1))
        .map { Util.faker.name().firstName() + " $it" }
        .subscribe(Util.newSubscriber())

    Thread.sleep(3000)
}