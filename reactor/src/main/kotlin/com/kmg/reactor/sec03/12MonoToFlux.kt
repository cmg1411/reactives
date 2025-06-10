package com.kmg.udemyreactor.sec03

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun main() {
    val fluxFromMono = Flux.from(getMono(1))
    save(fluxFromMono)

    // Flux 중 첫번쨰 값을 반환
    // 두가지 방법 가능
//    val monoFromFlux = Flux.just("1", "2", "3").next()
    val monoFromFlux = Mono.from(Flux.just("1", "2", "3"))
    save(monoFromFlux)
}

fun getMono(userId: Int): Mono<String> {
    return when(userId) {
        1 -> Mono.just("Tomas")
        2 -> Mono.empty()
        else -> Mono.error(IllegalArgumentException("User not found"))
    }
}

fun save(mono: Mono<String>) {
    mono.subscribe(Util.newSubscriber())
}

fun save(flux: Flux<String>) {
    flux.subscribe(Util.newSubscriber())
    flux.subscribe {}
}