package com.kmg.udemyreactor.sec01.publisher

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory

class SubscriptionImpl(
    private val subscriber: Subscriber<in String>,
    private val callable: () -> String,
) : Subscription {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val MAX_ITEMS = 10
    private var isCancelled = false
    private var count = 0

    override fun request(l: Long) {
        if (isCancelled) return

        logger.info("subscriber has requested $l items")

        if (l > MAX_ITEMS) {
            return subscriber.onError(RuntimeException("request size is too large"))
        }

        for (i in 0 until l) {
            if (count >= MAX_ITEMS) return
            count++
            subscriber.onNext(callable())

            if (count == MAX_ITEMS) {
                logger.info("no more data to produce")
                subscriber.onComplete()
                isCancelled = true
            }
        }
    }

    override fun cancel() {
        logger.info("subscriber has cancelled the subscription")
        this.isCancelled = true
    }
}
