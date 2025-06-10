package com.kmg.udemyreactor.sec02

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

private val log = LoggerFactory.getLogger("FromFuture")

// fromFuture 는 CompletableFuture 를 인자로 받는 함수와 FutureSupplier(Supplier<? extends CompletableFuture<? extends T>>)
// 를 인자로 받는 함수 두개 다 존재한다.
// CompletableFuture 를 인자로 받는 함수는 잘못 사용하면 (Mono.fromFuture(getName())) lazy 로딩이 안될 수 있음에 유의.
fun main() {
    Mono.fromFuture(getName())
        .subscribe(Util.newSubscriber("fromFuture"))

    Mono.fromFuture(getName()) // getName() 실행
    Mono.fromFuture { getName() } // getName() 실행안함. lazy

    Thread.sleep(500) // 메인 스레드 끝나는거 방지
}

fun getName(): CompletableFuture<String> {
    return CompletableFuture.supplyAsync {
        log.info("generating name")
        Util.faker.name().firstName()
    }
}