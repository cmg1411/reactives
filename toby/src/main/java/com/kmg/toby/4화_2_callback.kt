package com.kmg.toby

import mu.KotlinLogging
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

private val log = KotlinLogging.logger {}
/**
 * Future : 단순 비동기 처리 결과 핸들러. submit 으로만 받을 수 있다.
 * FutureTask : Future + Runnable. execute 를 사용해도 이걸로 비동기 결과를 받을 수 있다.
 * FutureTask + Callback
 */
fun main() {
    val es = Executors.newCachedThreadPool()

//    futureTaskBasic(es)
//    futureTaskIncludeCallback(es)
    handMadeCallbackFutureTask(es)

    es.shutdown()
}

fun futureTaskBasic(es: ExecutorService) {
    // FutureTask : Future 객체의 역할도 하는 Runnable 객체.
    // ExecutorService 의 execute() 에 인자로 넣을 수 있고, FutureTask 의 get() 을 통해 결과를 받을 수 있다.
    val ft = FutureTask<String> {
        Thread.sleep(2000)
        log.info("World")
        "Hello"
    }

    es.execute(ft)
    println(ft.get())
}

fun futureTaskIncludeCallback(es: ExecutorService) {
    val ft = object : FutureTask<String>({
        Thread.sleep(2000)
        log.info("World")
        "Hello"
    }) {
        // done 을 오버라이드 하면, get() 을 호출하지 않고도 직접 결과로 무언가의 처리를 할 수 있다.
        // 여기서는 익명 객체지만, 객체 생성시 callback 함수를 받아서 여기 적용해주면, callback 과 같은 동작을 하게 되는 것.
        // 그걸 아래서 해보자.
        override fun done() = log.info(get())
    }

    es.execute(ft)
}

fun handMadeCallbackFutureTask(es: ExecutorService) {
    val callableFutureTask = CallbackFutureTask(
        callable = {
            Thread.sleep(2000)
//            throw RuntimeException("Error!!!!!!") // 예외 던지기
            log.info("World")
            "Hello"
        },
        sc = { log.info(it) },
        ec = { log.error(it.message) }
    )

    es.execute(callableFutureTask)
}

class CallbackFutureTask(
    callable: () -> String,
    var sc: (String) -> Unit, // 인터페이스 만들어도 되나, 코틀린에서는 이걸로 가자.
    var ec: (Throwable) -> Unit // 인터페이스 만들어도 되나, 코틀린에서는 이걸로 가자.
) : FutureTask<String>(callable) {

    override fun done() = try {
        sc(get())
    } catch (e: InterruptedException) {
        // interrupt 예외가 발생하면 FutureTask 는 예외로 준다.
        // 인터럽트는 예외처리보단 시그널을 던지는게 중요. 아래처럼만 해도 충분.
        Thread.currentThread().interrupt()
    } catch (e: ExecutionException) {
        ec(e.cause!!) // ExecutionException 는 wrapping 된 것. cause 를 꺼내서 callback 에 넘긴다.
    }
}
