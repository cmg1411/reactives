package com.kmg.udemyreactor.sec07

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.LoopResources

private val log = LoggerFactory.getLogger("04EventLoopIssueFix")

// getProductName 사용자는 blocking operator 를 사용하고 있음.
// 아래 코드는 5 * 3 = 15 초가 걸린다.
// 이런 경우 publishOn 을 붙여서 다운스트림의 코드가 각기 다른 스레드에서 실행되도록 할 수 있다.
fun main() {
    for (i in 1..5) {
        getProductName(i)
            .map {
                Thread.sleep(3000)
                "$it-processed"
            }
            .subscribe(Util.newSubscriber())
    }

    Thread.sleep(10000)
}

fun getProductName(productId: Int): Mono<String> {
    return HttpClient.create()
        .runOn(LoopResources.create("eventLoopGroup", 1, true))
        .baseUrl("http://localhost:7070")
        .get()
        .uri("/demo01/product/$productId")
        .responseContent()
        .asString()
        .doOnNext { log.info("next: $it") }
        .next()
}
