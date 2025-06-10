package com.kmg.udemyreactor.sec02

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

private val log = LoggerFactory.getLogger("FromFuture")

// Publisher 생성을 지연시킬 수 있다.
// Mono 자체를 생성하기 위해 많은 지원을 소모해야하는 상황이 있다고 치자.
// 그리고 subscriber 가 있을지 없을지 모른다.
// 이 때 subscriber 가 subscribe 했을 때 Mono 를 생성하는게 효율적일 것이다.

// defer 는 Mono 를 생성하는 Supplier 를 인자로 받아서 MonoDefer 라는 Mono 하위 타입을 생성한다..
// defer 를 사용하면 subscribe() 함수가 불릴 때 supplier 실행한다. (request 아님)
// 1. 동적으로 달라지는 Mono 생성 로직이라 그때그떄 실행하고 싶은 경우 (분기문이라던지..)
// 2. 비싼 로직이 있는 Mono 생성이라 지연시키고 싶은 경우
// 3. 생성시 예외 가능성이 있고, 구독 시점에 발생시키고 싶은 경우
// 등등.. 이 있을 것.
fun main() {
//    createPublisher()
//        .subscribe(Util.newSubscriber("defer"))

    // 오래걸리는 Mono 생성을 실행하나, subscribe 는 일어나지 않음. 비효율적.
//    createPublisher()

    // 아무일 안일어남
//    Mono.defer { createPublisher() }

    // subscriber 가 subscribe() 를 호출하는 시점에 Mono 를 생성한다.
    Mono.defer { createPublisher() }
        .subscribe(Util.newSubscriber("defer"))
}

// Mono(Publisher) 를 생성하는게 오래걸리는 상황
fun createPublisher(): Mono<Int> {
    log.info("createPublisher() called")
    val list = listOf(1, 2, 3, 4, 5)
    doLogic(3, "prepare mono")
    return Mono.fromSupplier { longBusinessLogic(list) }
}

// 오래걸리는 비즈니스 로직
fun longBusinessLogic(list: List<Int>): Int {
    log.info("Business Logic")
    doLogic(5, "doLogic")
    return list.sum()
}

fun doLogic(time: Long, task: String) {
    for(i in 1..time) {
        log.info("$task $i")
        Thread.sleep(1000)
    }
}