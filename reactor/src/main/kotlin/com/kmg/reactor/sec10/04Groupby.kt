package com.kmg.reactor.sec10

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux
import java.time.Duration

// 키값으로 사용할 연산을 지정할 수 있다. 해당 키값으로 그룹핑된다.
// Flux<GroupedFlux<K, T>> 를 반환한다.
// groupBy 사용시 카디널리티에 주의한다. 낮아야 한다.
fun main() {
    Flux.range(1, 30)
        .delayElements(Duration.ofSeconds(1))
        .groupBy { it % 2 }
        .flatMap { groupedFlux ->
            groupedFlux
                .map { "Group: ${groupedFlux.key()} - Event: $it" }
        }
        .subscribe(Util.newSubscriber())

    Thread.sleep(60000)
}