package com.kmg.udemyreactor.sec05

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux
import java.time.Duration

// 특정 간격마다 값을 방출한다.
// log 를 찍어보면 알겠지만, 기존에 만들어논 subscriber request(Long.MAX_VALUE) 라 unbounded request 를 요청한다.
// LambdaMonoSubscriber, LambdaSubscriber 도 마찬가지다.
// delay 가 붙으면, 딜레이를 가지고 request(1) 로 하나씩 가져온다. 따라서 publisher 가 불필요한 일을 미리 하지 않을 수 있다.
fun main() {
    Flux.range(1, 10)
        .log()
        .delayElements(Duration.ofSeconds(1))
//        .delayUntil {  }
//        .delaySequence()
//        .delaySubscription()
        .subscribe(Util.newSubscriber())

    Thread.sleep(12000)
}