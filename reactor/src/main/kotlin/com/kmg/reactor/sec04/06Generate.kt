package com.kmg.udemyreactor.sec04

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux

private val log = LoggerFactory.getLogger("0406")

// generate 는 Flux 에서 programmatical 하게 데이터를 방출할 수 있다.
// Consumer<SynchronousSink<T>> 를 인자로 받는다. 이를 매 request 마다 매번 새로 실행한다.
// 즉, Consumer<SynchronousSink<T>> 는 상태가 없는 람다 함수다.
// Consumer<SynchronousSink<T>> 내부에서 next 는 한번만 호출할 수 있다.
// generate 는 기본적으로 무한 방출하며 take, takeUntil, takeWhile 등으로 제어할 수 있다.
// create 와 다르게 방출 루프가 자동으로 관리된다. 해서 next 는 한번밖에 안되는것.
// 그럼 다음 루프에 바뀌어야되는 상태를 가진 상황은? 상태를 인자로 넣을 수 있다.
fun main() {
    Flux.generate {
        log.info("Generating value")
        val country = Util.faker.country().name()
        it.next(country)
    }
        .takeUntil { it.lowercase() == "canada" }
        .subscribe(Util.newSubscriber())
}