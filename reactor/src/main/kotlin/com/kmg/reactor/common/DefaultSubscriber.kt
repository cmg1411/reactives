package com.kmg.udemyreactor.common

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory

class DefaultSubscriber<T>(
    // 하나의 publisher 에 여러 subscriber 붙을 수 있음.
    // 누가 받았고 누가 못받았는지 판별하기 위해 이름 지어줘야함.
    private val name: String,
) : Subscriber<T> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun onSubscribe(subscription: Subscription) {
        // reactor 의 subscriber 스타일. LambdaMonoSubscriber 참고.
        subscription.request(Long.MAX_VALUE)
    }

    override fun onNext(value: T) {
        logger.info("[$name] received : $value")
    }

    override fun onComplete() {
        logger.info("[$name] completed!")
    }

    override fun onError(throwable: Throwable) {
        logger.info("[$name] error : $throwable")
    }
}
