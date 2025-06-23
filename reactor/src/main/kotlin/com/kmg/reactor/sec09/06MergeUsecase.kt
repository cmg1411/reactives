package com.kmg.reactor.sec09

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux
import java.time.Duration

// 여러 소스의 데이터를 합치는 예시 가능
// 스카이 스캐너 예시를 구현해보자.
fun main() {
    val 대한한공 = 대한한공()
    val 아시아나 = 아시아나()
    val 예미례이츠 = 예미례이츠()

    Flux.merge(
        대한한공.getFlights(),
        아시아나.getFlights(),
        예미례이츠.getFlights()
    )
        .take(15)
        .subscribe(Util.newSubscriber("mergeDemo"))

    Thread.sleep(10000)
}

data class Flight(
    val airline: String,
    val price: Long,
)

class 대한한공 {

    private val AIRLINE = "대한한공"

    fun getFlights(): Flux<Flight> {
        return Flux.range(1, Util.faker.random().nextInt(5, 10))
            .delayElements(Duration.ofMillis(Util.faker.random().nextLong(1200)))
            .map { Flight(AIRLINE, Util.faker.random().nextLong(1_000_000)) }
            .transform(Util.fluxLogger(AIRLINE))
    }
}

class 아시아나 {

    private val AIRLINE = "아시아나"

    fun getFlights(): Flux<Flight> {
        return Flux.range(1, Util.faker.random().nextInt(2, 6))
            .delayElements(Duration.ofMillis(Util.faker.random().nextLong(5000)))
            .map { Flight(AIRLINE, Util.faker.random().nextLong(5_000_000)) }
            .transform(Util.fluxLogger(AIRLINE))
    }
}

class 예미례이츠 {

    private val AIRLINE = "예미례이츠"

    fun getFlights(): Flux<Flight> {
        return Flux.range(1, Util.faker.random().nextInt(5, 10))
            .delayElements(Duration.ofMillis(Util.faker.random().nextLong(3000)))
            .map { Flight(AIRLINE, Util.faker.random().nextLong(10_000_000)) }
            .transform(Util.fluxLogger(AIRLINE))
    }
}