package com.kmg.udemyreactor.sec01.publisher

import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber

class PublisherImpl(
    val callable: () -> String,
) : Publisher<String> {

    override fun subscribe(subscriber: Subscriber<in String>) {
        val subscription = SubscriptionImpl(subscriber, callable)
        subscriber.onSubscribe(subscription)
    }
}