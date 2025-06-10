package com.kmg.udemyreactor.sec02

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

private val log = LoggerFactory.getLogger("FromRunnable")

// fromRunnable 은 fromCallable 과 비슷하지만 Runnable 과 Callable 의 차이와 같다. 리턴값이 없다.
// 마찬가지로 lazy 동작이기 때문에, subscriber 가 없으면 동작하지 않는다.
fun main() {
    getProductName(1)
        .subscribe(Util.newSubscriber())
}

fun getProductName(productId: Int): Mono<String> {
    if (productId == 1) {
        return Mono.fromSupplier { Util.faker.commerce().productName() }
    }

    return Mono.fromRunnable { notifytBusiness(productId) }
}

fun notifytBusiness(productId: Int) {
    log.info("notify unavailable productId : $productId")
}