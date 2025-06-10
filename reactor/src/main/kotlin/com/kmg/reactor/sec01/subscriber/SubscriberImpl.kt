package com.kmg.udemyreactor.sec01.subscriber

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory

class SubscriberImpl : Subscriber<String> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    lateinit var subscription: Subscription
        private set

    override fun onSubscribe(subscription: Subscription) {
        this.subscription = subscription
    }

    override fun onNext(email: String) {
        logger.info("received : $email")
    }

    override fun onComplete() {
        logger.info("completed!")
    }

    override fun onError(throwable: Throwable) {
        logger.info("error : $throwable")
    }
}