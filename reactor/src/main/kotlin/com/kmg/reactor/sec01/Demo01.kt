package com.kmg.udemyreactor.sec01

import com.kmg.udemyreactor.common.Util.faker
import com.kmg.udemyreactor.sec01.publisher.PublisherImpl
import com.kmg.udemyreactor.sec01.subscriber.SubscriberImpl
import java.time.Duration

fun main() {
    demo1()
    // subscriber 가 계속 request 를 하더라도, publisher 입장에서 보낼 데이터가 없다면 아무것도 안 보낼 수 있다.
        // request 에 complete 된 이후 request 는 무시하도록 코드 구현이 따로 필요하다.
//    demo2() // subscriber 는 subscription 을 통해 request 를 할 수 있다.
//    demo3() // subscriber 는 subscription 을 통해 cancel 이 가능.
//    demo4() // subscriber 는 subscription 을 통해 request 를 하던 중 error 를 만날 수 있고, 그 때 subscriber 에서 정의한 error 동작을 처리할 수 있다.(onError)
}

fun demo1() {
    val publisher = PublisherImpl { faker.internet().emailAddress() }
    val subscriber = SubscriberImpl()
    publisher.subscribe(subscriber)

    subscriber.subscription.request(2)
    Thread.sleep(2000)
}

fun demo2() {
    val publisher = PublisherImpl { faker.internet().emailAddress() }
    val subscriber = SubscriberImpl()
    publisher.subscribe(subscriber)

    subscriber.subscription.request(3)
    Thread.sleep(2000)
    subscriber.subscription.request(3)
    Thread.sleep(2000)
    subscriber.subscription.request(3)
    Thread.sleep(2000)
    subscriber.subscription.request(3)
    Thread.sleep(2000)
    subscriber.subscription.request(3)
    Thread.sleep(2000)
}

fun demo3() {
    val publisher = PublisherImpl { faker.internet().emailAddress() }
    val subscriber = SubscriberImpl()
    publisher.subscribe(subscriber)

    subscriber.subscription.request(3)
    Thread.sleep(2000)
    subscriber.subscription.cancel()
    subscriber.subscription.request(3)
    Thread.sleep(2000)
}

fun demo4() {
    val publisher = PublisherImpl { faker.internet().emailAddress() }
    val subscriber = SubscriberImpl()
    publisher.subscribe(subscriber)

    subscriber.subscription.request(11)
}

