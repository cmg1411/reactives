package com.kmg.udemyreactor.sec07

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

private val log = LoggerFactory.getLogger("multiSubscribeOn")

// 여러개의 subscribeOn 이 붙으면 순서대로 스케줄러가 바뀐다.
// 올라가다 만난 subscribeOn 의 스케줄러로 바뀐다는 소리.
// 올라가다가 마지막에 만난 subscribeOn 의 스케줄러로 나머지 모든게 처리된다.
// 아래 예시에서 first2 는 Thread-0, first1 은 boundedElastic, 나머지는 모두 new-parallel 에서 처리된다.

// 라이브러리 사용시 이 주제에 대해 알아야 할 점은,
// 라이브러리들이 생성하는 Mono, Flux 에는 대부분 subscribeOn 을 붙여놨다.
// 따라서 해당 라이브러리르 사용하여 만든 스트림에서 subscribeOn 을 붙여도 라이브러리 개발자가 붙여놓은 subscribeOn 이 가장 publisher 와 가까워서
// 결국 라이브러리에서 정의 해당 스케줄러에서 publisher 가 실행 될 것이다.
// 따라서 라이브러리 사용할 때 subscribeOn 붙여놓고 왜 안되지? 하지 말자.
fun main() {
    val flux = Flux.create { sink ->
        for (i in 1..3) {
            log.info("generating: $i")
            sink.next(i)
        }
        sink.complete()
    }
        .subscribeOn(Schedulers.newParallel("new-parallel"))
        .doOnNext { log.info("doOnNext: $it") }
        .doFirst { log.info("first1") }
        .subscribeOn(Schedulers.boundedElastic())
        .doFirst { log.info("first2") }

    val runnable =  Runnable { flux.subscribe(Util.newSubscriber()) }
    Thread.ofPlatform().start(runnable)

    Thread.sleep(3000)
}
