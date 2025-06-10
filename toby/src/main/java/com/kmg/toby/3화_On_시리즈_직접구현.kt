package com.kmg.toby

import mu.KotlinLogging
import org.springframework.scheduling.concurrent.CustomizableThreadFactory
import java.util.concurrent.Executors
import java.util.concurrent.Flow.*

private val log = KotlinLogging.logger {}

/**
 * 스케줄러를 지정하는 방법
 * - publishOn : 데이터 흐름의 발행자가 동작하는 스케줄러 지정
 * - subscribeOn : 데이터 흐름의 구독자가 동작하는 스케줄러 지정
 */
fun main() {
//    subscribeOnTest()
//    publishOnTest()
    publishOnSubscribeOnTest()

    log.info("EXIT")
}

val pub = Publisher { sub ->
    sub.onSubscribe(object : Subscription {
        override fun request(n: Long) {
            log.info("request")
            sub.onNext(1)
            sub.onNext(2)
            sub.onNext(3)
            sub.onNext(4)
            sub.onNext(5)
            sub.onComplete()
        }

        override fun cancel() {
        }
    })
}

val subscriber = object : Subscriber<Int> {
    override fun onSubscribe(subscription: Subscription) {
        log.info("onSubscribe")
        subscription.request(Long.MAX_VALUE)
    }

    override fun onNext(item: Int) {
        log.info("onNext $item")
    }

    override fun onComplete() {
        log.info("onComplete")
    }

    override fun onError(throwable: Throwable) {
    }
}

fun subscribeOnTest() {
    val subOnPub = Publisher { sub ->
        val es = Executors.newSingleThreadExecutor(object : CustomizableThreadFactory() {
            override fun getThreadNamePrefix() = "subOn-"
        }) // 단 한개의 스레드만 동작하고, 나머지 작업은 queue 에 저장.
        es.execute { pub.subscribe(sub) }
    }

//    pub.subscribe(subscriber) // main thread 를 block 한다.
    subOnPub.subscribe(subscriber) // main thread 를 block 하지 않고 바로 빠져 나온다.
}

val publishOnPub = Publisher { sub ->
    pub.subscribe(object : Subscriber<Int> {
        private val es = Executors.newSingleThreadExecutor(object : CustomizableThreadFactory() {
            override fun getThreadNamePrefix() = "pubOn-"
        })

        override fun onSubscribe(subscription: Subscription) {
            sub.onSubscribe(subscription)
        }

        override fun onNext(item: Int) {
            es.execute { sub.onNext(item) }
        }

        /**
         * 여기선 컨테이너 환경이 아니고 테스트용이므로 스레드풀을 재사용할 필욘 없어서,
         * onComplete, onError 에서 shutdown 을 해준다.
         */
        override fun onComplete() {
            es.execute { sub.onComplete() }
            es.shutdown()
        }

        override fun onError(throwable: Throwable) {
            es.execute { sub.onError(throwable) }
            es.shutdown()
        }
    })
}

fun publishOnTest() {
    publishOnPub.subscribe(subscriber)
}

fun publishOnSubscribeOnTest() {
    val pubOnSubOnPub = Publisher { sub ->
        val es = Executors.newSingleThreadExecutor(object : CustomizableThreadFactory() {
            override fun getThreadNamePrefix() = "subOn-"
        })
        es.execute { publishOnPub.subscribe(sub) }
        es.shutdown()
    }

    pubOnSubOnPub.subscribe(subscriber)
}
