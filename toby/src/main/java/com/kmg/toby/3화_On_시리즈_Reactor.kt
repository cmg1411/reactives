package com.kmg.toby

import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

fun main() {
    Flux.range(1, 10)
//        .publishOn(Schedulers.newSingle("pub"))
        .log() // 어느 스레드에서 도는지 알기 좋음
        .subscribeOn(Schedulers.newSingle("sub"))
        .subscribe(::println)

    /**
     * 내부적으로 singleThreadExecutor 가 생성 될 것.
     * publisher 하나에 여러 subscriber 가 있을 수 있음. 따라서 singleThreadExecutor 를 재사용하기에 shutdown 하지 않음.
     *
     * netty / tomcat 안에서 실행하면 관리가 될 것. 하지만 여기서는 main 스레드에서 사용하기에 종료되지 않음.
     */

    println("EXIT")
}