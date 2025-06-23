package com.kmg.reactor.sec11

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

// Publisher 는 onComplete, onError 를 만나면 publish 를 종료.
// repeat : onComplete() 로 끝난 Publisher 를 다시 시작.
// Mono.repeat() 는 flux 를 반환.
// 인자로 숫자 안주면 무한 반복
fun main() {
    demo4()

    // for 문과는 다르다. 리엑터는 non-blocking 이기 때문에 for 문 내부의 결과가 끝나기 전에 다음 루프 진행.
    // repeat 는 onComplete() 에 의해 다시 시작되므로, for 문과는 다르게 동작함.
//    for (i in 1..3) mono.subscribe(subscriber)

    Thread.sleep(10000)
}

private fun demo1() {
    getCountryName()
        .repeat(3)
        .subscribe(Util.newSubscriber())
}

private fun demo2() {
    getCountryName()
        .repeat()
        .takeUntil { it == "Canada" }
        .subscribe(Util.newSubscriber())
}

fun demo3() {
    val count = AtomicInteger(0)
    getCountryName()
        .repeat { count.incrementAndGet() < 4 }
        .subscribe(Util.newSubscriber())
}

fun demo4() {
    getCountryName()
        .repeatWhen { it.delayElements(Duration.ofMillis(1000)).take(3) }
        .subscribe(Util.newSubscriber())
}


private fun getCountryName(): Mono<String> {
    return Mono.fromSupplier { Util.faker.country().name() }
}