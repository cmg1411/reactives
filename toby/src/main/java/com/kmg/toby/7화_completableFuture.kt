package com.kmg.toby

import mu.KotlinLogging
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}
/**
 * 1. runAsync : runnable 인자
 * 2. supplyAsync : supplier 인자
 *  - thenAccept : consumer 를 인자로 받음. 반환값 없음. 마무리 체인.
 *  - thenApply : 콜백을 남는 스레드에서 실행하고, 리턴값을 반환. 예시 코드에서는 main 이 쓰임.
 *      - 이전 작업이 끝났다면 같은 스레드에서 동작, 이전 작업이 blocking 되어있다면 forkJoinPool worker 에서 동작.
 *      - 무조건 콜러 스레드와 같은 스레드에서 작업하는건 아니다.
 *  - thenApplyAsync : 콜백을 forkJoinPool worker 에서 실행하고 리턴값을 반환
 *      - 무조건 콜러 스레드와 다른 스레드에서 동작.
 * 3. thenCompose : 람다식의 반환값이 CompletableFuture 인 경우 사용.
 *  - supplyAsync 는 타입 파라미터 지정된 타입의 리턴값을 CompletableFuture 로 감싸지만
 *  - thenCompose 는 람다식의 반환값 자체가 CompletableFuture 일 때 사용.
 *  - 이건 쓸때가 있다는데.. 아직 잘 모르겠음
 */
/**
 * thenApplyAsync(Function<? super T,? extends U> fn) : 기본 스레드풀 (forkJoinPool) 에서 동작
 * thenApplyAsync(Function<? super T,? extends U> fn, Executor executor) : 지정한 스레드풀에서 동작
 */
fun main() {
    // 콜백 메서드들은 같은 스레드에서 동작한다.
    CompletableFuture.runAsync { log.info { "RunAsync" } } // ForkJoinPool.commonPool-worker-1
        .thenRun { log.info { "ThenRun1" } } // ForkJoinPool.commonPool-worker-1
        .thenRun { log.info { "ThenRun2" } } // ForkJoinPool.commonPool-worker-1
    log.info { "E XIT" }

    // 결과값을 반환하여 체인에서 사용가능한 supply 시리즈.
    CompletableFuture.supplyAsync {
        log.info { "SupplyAsync" }
//        Thread.sleep(1000L)
//        throw RuntimeException("Error") // ThenAccept1, ThenAccept2 는 실행되지 않음.
        1
    }.thenApply {
        log.info { "ThenAccept1 $it" }
        it + 1
    }.thenCompose {
        log.info { "ThenCompose $it" }
        CompletableFuture.completedFuture(it + 1)
    }.thenApplyAsync {
        log.info { "ThenAccept2 $it" }
        it + 1
    }.exceptionally {
        -10 // 위의 모든 체인에서 예외가 발생하면 동작. 예외가 발생하면 다른 예외로 랩핑하는 작업 공통처리 가능.
    }.thenAccept { log.info { "ThenAccept3 $it" } }

    ForkJoinPool.commonPool().shutdown()
    ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS)
}

