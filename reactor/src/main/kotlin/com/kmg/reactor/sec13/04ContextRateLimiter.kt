package com.kmg.reactor.sec13

import com.kmg.reactor.sec13.client.ExternalServiceClient
import com.kmg.reactor.sec13.client.RateLimiter
import com.kmg.udemyreactor.common.Util
import reactor.util.context.Context

fun main() {

    val client = ExternalServiceClient()
    RateLimiter.refresh()

    for (i in 1..20) {
        client.getBook()
            .contextWrite(Context.of("user", "mike")) // prime
//            .contextWrite(Context.of("user", "sam")) // standard
            .subscribe(Util.newSubscriber())
        Thread.sleep(1000)
    }

    Thread.sleep(5000)
}
