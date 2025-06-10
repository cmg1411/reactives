package com.kmg.udemyreactor.sec05

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux

fun main() {
    Flux.range(1, 10)
        .handle { integer, sink ->
            when(integer) {
                1 -> sink.next("one")
                3 -> {}
                4 -> sink.next("four")
                6 -> sink.error(IllegalArgumentException("six"))
                else -> sink.next(integer)
            }
        }
        .subscribe(Util.newSubscriber())

    Flux.generate { it.next(Util.faker.country().name()) }
        .handle { country, sink ->
            sink.next(country)
            if (country.lowercase() == "canada") {
                sink.complete()
            }
        }
        .subscribe(Util.newSubscriber())
}
