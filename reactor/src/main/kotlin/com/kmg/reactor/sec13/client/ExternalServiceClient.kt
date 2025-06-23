package com.kmg.reactor.sec13.client

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient

class ExternalServiceClient {

    fun getBook(): Mono<String> {
        return HttpClient.create()
            .baseUrl("http://localhost:7070")
            .get()
            .uri("/demo07/book")
            .responseContent()
            .asString()
            .startWith(RateLimiter.limitCalls())
            .contextWrite(UserService.userCategoryContext())
            .next()
    }
}
