package com.kmg.reactor.sec09

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux
import java.time.Duration

// zip 은 각 프로듀서에게서 하나씩 받아서 그걸 합쳐서 방출한다.
// 즉 프로듀서 세개를 합치면 세개짜리 튜플을 방출, 네개를 합치면 네개짜리 튜플을 방출한다.
// 각 프로듀서에서 독립적으로 다 방출하는 merge 와 다르다.
// 프로듀서 중 방출이 끝난게 하나라도 있다면 합치는 작업이 안되므로 그 때 completed 된다.
// Tuple<type1, type2, type3...> 형태로 방출된다. 해서 어떤 타입의 프로듀서든 넣을 수 있다.
fun main() {
    Flux.zip(
        getBody(),
        getEngine(),
        getTires()
    )
        .map {
            Car(
                body = it.t1,
                engine = it.t2,
                tires = it.t3
            )
        }
        .subscribe(Util.newSubscriber("car factory"))

    Thread.sleep(5000)
}

fun getBody(): Flux<String> {
    return Flux.range(1, 5)
        .map { "body-$it" }
        .delayElements(Duration.ofMillis(500))
}

// 가장 적은 이 프로듀서에 맞춰서 zip 의 결과는 3개다.
fun getEngine(): Flux<String> {
    return Flux.range(1, 3)
        .map { "engine-$it" }
        .delayElements(Duration.ofMillis(200))
}

fun getTires(): Flux<String> {
    return Flux.range(1, 10)
        .map { "tires-$it" }
        .delayElements(Duration.ofMillis(75))
}

data class Car(
    val body: String,
    val engine: String,
    val tires: String
)