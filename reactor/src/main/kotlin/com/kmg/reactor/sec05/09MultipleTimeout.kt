package com.kmg.udemyreactor.sec05

import com.kmg.udemyreactor.common.Util
import io.netty.handler.timeout.TimeoutException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import kotlin.concurrent.thread

// publisher 가 방출하는 아이템에 timeout 을 걸 수 있다.
// 방출하는 아이템 사이의 기간. 첫 아이템이라면 첫 아이템이 방출하는 시간.
// timeout 에 대해서는 좀 더 알아 볼 필요 있음.
// subscriber, publisher 가 같은 스레드일 때 Mono.fromSupplier 의 로직이 오래 걸리는 거라면 timeout 이 걸리지 않음.
// 반면 subscirber, publisher 가 다른 스레드라면 timeout 이 걸림.
// delayElement 도 동작이 다른 스레드에서 subscriber 가 동작하는 원리.

// timeout 은 스케줄러를 통해 동작하며, 기본적으로 Schedulers.parallel() 사용
// timeout 은 시간 지나면 실행할 fallback publisher 를 지정 가능
fun main() {

    val mono = Mono.fromSupplier {
        "service - ${Util.faker.commerce().productName()}"
    }

    // timeout
    mono
        .delayElement(Duration.ofSeconds(3), Schedulers.boundedElastic())
        .timeout(Duration.ofSeconds(2))
        .onErrorReturn(TimeoutException::class.java, "error default name when timeout")
        .subscribe(Util.newSubscriber())

    // fallback Mono 지정
    mono
        .delayElement(Duration.ofSeconds(3), Schedulers.boundedElastic())
        .timeout(Duration.ofSeconds(2), Mono.just("error default name when timeout"))
        .subscribe(Util.newSubscriber())

//    val longMono = Mono.fromSupplier {
//        while(true) {}
//        "service - ${Util.faker.commerce().productName()}"
//    }
//
//    // timeout 안남
//    longMono
//        .timeout(Duration.ofSeconds(3))
//        .subscribe(Util.newSubscriber())
//
//    // timeout 발생
//    longMono
//        .subscribeOn(Schedulers.boundedElastic())
//        .timeout(Duration.ofSeconds(3))
//        .subscribe(Util.newSubscriber())

    Thread.sleep(5000)
}
