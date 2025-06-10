package com.kmg.udemyreactor.sec12

import org.slf4j.LoggerFactory
import reactor.core.publisher.Sinks
import java.util.concurrent.CompletableFuture

private val log = LoggerFactory.getLogger("SinkThreadSafety")

// tryEmitNext 는 thread safe 하지 않다.
// emitNext + failureHandler = thread safe
// failureHandler 에서 true 를 리턴하면 재시도 한다.
// FAIL_NON_SERIALIZED 한 에러를 받았을때 계속해서 재시도하여 성공할 때 까지 하는 그런 개념.
// 이런 방식이 아니더라도 동시성 문제 때문에 FAIL_NON_SERIALIZED 발생시 failureHandler 에 실행할 fallback 로직을 적절히 정의하고, 재시도하든 말든 하면 된다.
fun main() {
    val sink = Sinks.many().unicast().onBackpressureBuffer<Int>()
    val flux = sink.asFlux()

    val list = mutableListOf<Int>()
    flux.subscribe(list::add)

    for (i in 1..1000) {
        val j = i
        CompletableFuture.runAsync {
//            sink.tryEmitNext(j) // non-thread-safe
            sink.emitNext(j) { signalType, emitResult ->
                return@emitNext emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED
            }
        }
    }

    Thread.sleep(1000)
    log.info("list: ${list.size}")
}