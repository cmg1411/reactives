package com.kmg.toby

import mu.KotlinLogging
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.Flow.*
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {  }

fun main() {
//    libIntervalTake()
    handMadeIntervalTake()
}

fun libIntervalTake() {
    Flux.interval(Duration.ofMillis(1000))
        .take(5) // 5개만 받고 종료
        .subscribe { log.info { it } }

    TimeUnit.SECONDS.sleep(7)
}

fun handMadeIntervalTake() {
    val pub = Publisher { sub ->
        sub.onSubscribe(object : Subscription {
            private var no = 0
            private var canceled = false

            override fun request(n: Long) {
                val es = Executors.newSingleThreadScheduledExecutor()
                es.scheduleAtFixedRate({
                    if (canceled) {
                        es.shutdown()
                        return@scheduleAtFixedRate
                    }
                    sub.onNext(no++) }, 0, 300, TimeUnit.MILLISECONDS)
            }

            override fun cancel() {
                canceled = true
            }
        })
    }

    val sub = object : Subscriber<Int> {
        override fun onSubscribe(subscription: Subscription) {
            log.info { "onSubscribe" }
            subscription.request(Long.MAX_VALUE)
        }

        override fun onNext(item: Int) {
            log.info { "onNext : $item" }
        }

        override fun onComplete() {
            log.info { "onComplete" }
        }

        override fun onError(throwable: Throwable) {
            log.info { "onError" }
        }
    }

    val TAKE_COUNT = 5
    val taskPub = Publisher { s ->
        pub.subscribe(object : Subscriber<Int> {
            private var count = 1
            private lateinit var subscription: Subscription

            override fun onSubscribe(subscription: Subscription) {
                this.subscription = subscription
                s.onSubscribe(subscription)
            }

            override fun onNext(item: Int) {
                s.onNext(item)
                if (count++ >= TAKE_COUNT) subscription.cancel()
            }

            override fun onComplete() {
                sub.onComplete()
            }

            override fun onError(throwable: Throwable) {
                sub.onError(throwable)
            }
        })
    }

    taskPub.subscribe(sub)
    // 유저 스레드 이기에 Thread.sleep 필요 X
}

