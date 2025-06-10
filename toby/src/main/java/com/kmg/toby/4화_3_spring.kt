package com.kmg.toby

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Future

private val log = KotlinLogging.logger {}
/**
 * Spring 에서의 비동기 처리.
 * 1. Future 반환 : get() 을 호출 해줘야 한다. (Blocking)
 * 2. ListenableFuture 반환 : addCallback() 을 통해 callback 을 등록해 줄 수 있다. (Non-Blocking)
 * 3. CompletableFuture 반환 : 이후에 알아 보자.
 */
@RestController
class `4화_spring`(
    private val asyncService: AsyncService
) {

    @GetMapping("/async")
    fun asyncTest() {
        log.info { "Start" }
        val future = asyncService.async()
        log.info { "End if ${future.isDone}" } // false
        log.info { "Result : ${future.get()}" } // blocking
        log.info { "EXIT" } // 마지막에 찍힘
    }

    @GetMapping("/async2")
    fun asyncTest2(): String {
        log.info { "Start" }
        val future = asyncService.async2().apply {
            addCallback(
                { log.info { "Success : ${get()}" } }, // EXIT, return 이후에 찍힘 (Callback, Non-Blocking)
                { log.info { "Fail : ${it.message}" } }
            )
        }
        log.info { "End if ${future.isDone}" } // false
        log.info { "EXIT" } // 먼저 찍힘
        return "OK" // 이거도 먼저 찍힘
    }
}

@Service
class AsyncService {
    @Async
    fun async(): Future<String> {
        log.info { "World" }
        Thread.sleep(2000)
        return AsyncResult("Hello")
    }

    @Async
    fun async2(): ListenableFuture<String> {
        log.info { "World" }
        Thread.sleep(2000)
        return AsyncResult("Hello")
    }
}

/**
 * @Async 를 그대로 쓰면 좋지 않다.
 * SimpleAsyncTaskExecutor 가 기본적으로 쓰이는데, 이건 스레드들을 캐싱해서 풀로 사용하지 않고, 계에에속 생산한다.
 * ExecutorService 를 직접 빈으로 만들어서 @Async 안에 인자로 줘서 사용하자.
 *
 * 참고 : ThreadPoolTaskExecutor 는 JMX 로 runtime 에 값을 바꿀 수 있다.
 */
