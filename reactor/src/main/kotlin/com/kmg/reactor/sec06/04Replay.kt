package com.kmg.udemyreactor.sec06

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import java.time.Duration

private val log = LoggerFactory.getLogger("03Replay")

// replay()
// hot publisher 에서 중간에 들어온 구독자가 처음부터의 데이터를 받고 싶을 때 사용.
// replay 는 값을 캐싱하고 있다가, 새 subscriber 가 들어오면 캐싱된 값을 모두 emit 한다.
// cold pub 과 다른점은, 구독자가 등록된 이후는 하나의 파이프라인에서 n 개의 구독자가 같은 타이밍에 같은 데이터를 받고 싶지만 (hot pub 의 요구사항) 이전의 데이터를 모두 받고 시작하고 싶을 때 사용.
// 인자 값으로 몇개의 값을 캐싱할지 결정할 수 있고 default 는 MAX_VALUE(모두 캐싱) 이다.
fun main() {
    val stockFlux = stock().replay().autoConnect(0)

    Thread.sleep(3000)

    log.info("Tomas Started !!")
    stockFlux
        .subscribe(Util.newSubscriber("Tomas"))

    Thread.sleep(3000)

    log.info("Jonny Started !!")
    stockFlux
        .subscribe(Util.newSubscriber("Jonny"))

    Thread.sleep(20000)
}

fun stock(): Flux<Int> {
    return Flux.generate { it.next(Util.faker.random().nextInt(1000)) }
        .delayElements(Duration.ofSeconds(1))
        .doOnNext { log.info("emitting price : $it") }
}