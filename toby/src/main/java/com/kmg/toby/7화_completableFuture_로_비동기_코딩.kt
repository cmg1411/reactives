package com.kmg.toby

import com.study.coroutine.reactive.config.MyService
import io.netty.channel.nio.NioEventLoopGroup
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.http.client.Netty4ClientHttpRequestFactory
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.AsyncRestTemplate
import org.springframework.web.context.request.async.DeferredResult
import java.util.concurrent.CompletableFuture

private val log = KotlinLogging.logger {}

@RestController
class `7화_completableFuture_로_비동기_코딩`(
    private val myService: MyService
) {

    @GetMapping("/completablefuture")
    fun completableFuture(idx: String): DeferredResult<String> {
        val art = AsyncRestTemplate(Netty4ClientHttpRequestFactory(NioEventLoopGroup(1)))

        val deferredResult = DeferredResult<String>()

        art.getForEntity("$REMOTE_SLOW_URI${idx}", String::class.java).toCF()
//            .thenApplyAsync { art.getForEntity("$REMOTE_SLOW2_URI${idx}", String::class.java, it) } // 이렇게 되면 CompletableFuture<ListenableFuture<*>> 가 됨. 따라서 내부를 CF 로 변환 후 사용.
            .thenCompose {
//                throw IllegalArgumentException("error 테스트") // 예외 발생 테스트. 2초 걸림. (처음의 2초 호출만 실행했고, 이후 호출은 무시됨)
                art.getForEntity("$REMOTE_SLOW2_URI${it.body}", String::class.java).toCF()
            }
            .workWithAsync()
            .thenAccept {
                deferredResult.setResult(it)
            }
            .exceptionally { // thenCompose, thenAccept 모두 에서 발생한 예외를 처리
                deferredResult.setErrorResult(it.message!!)
                return@exceptionally null // exceptionally() 의 인자가 Function 밖에 없음.. 일부러 null 리턴 해줌. 의미는 없음.
            }

        return deferredResult
    }

    // 비동기 처리 선택지들.
    fun CompletableFuture<ResponseEntity<String>>.workWithAsync(): CompletableFuture<String> {
        // 1. @Async 가 붙은 비동기 함수를 실행하는 방법.
//        return this.thenCompose { myService.workAsync(it.body!!).toCF() }  // 마찬가지로 AsyncResult(ListenableFuture 하위 클래스) 이므로 cf 로 변환
        // 2. @Async 가 없는 일반 메서드 실행. 앞선 작업의 스레드에서 실행.
        return this.thenApply {
            Thread.sleep(1000L)
            myService.work(it.body!!) }
        // 3. ~Async 시리즈. ForkJoinPool 스레드로 비동기 실행.
//        return this.thenApplyAsync { myService.work(it.body!!) }
    }
}

// AsyncRestTemplate 은 ListenableFuture 를 반환.
// 따라서 CompletableFuture 로 변환해주는 확장함수를 만들어준다.
fun <T> ListenableFuture<T>.toCF(): CompletableFuture<T> {
    val completableFuture = CompletableFuture<T>()
    addCallback(
        { result -> completableFuture.complete(result) },
        { ex -> completableFuture.completeExceptionally(ex) }
    )
    return completableFuture
}

fun main() {
    // 4.~초
    performanceTestRefactored("http://localhost:8080/completablefuture?idx={idx}")
}