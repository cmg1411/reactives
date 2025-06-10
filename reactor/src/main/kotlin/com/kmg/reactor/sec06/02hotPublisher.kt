package com.kmg.udemyreactor.sec06

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import java.time.Duration

private val log = LoggerFactory.getLogger("02hotPublisher")

// hot pub 은 스트리밍 파이프라인이 한개이다.
// 구독자가 중간에 추가되면 해당 구독자는 데이터 중간부터 받는다.
// 등록된 n 개의 구독자는 onNext 시점에 같은 데이터를 같은 시점에 받는다.

// hot pub 만드는 방법 : share()

// share() == publish().refCount(1)
// refCount(2) 는 두개 이상의 구독자가 있어야만 pub emit 이 시작된다.
// refCount(2) 라도, 시작이 아닌 종료 시점은 한명만 있어도 보장됨.

// re-subscribe
// 모든 구독자의 구독이 끝나서 종료된 hot publisher 에 다시 구독을 요청하면 처음부터 다시 시작한다.

// 구독자가 없어도 publisher 가 emit 하도록 하는 방법은 autoConnect(0) 을 사용하면 된다.
// refCount, autoConnect 둘 다 최소 구독자의 개수를 조절하는 메서드인데,
// refCount 는 구독자가 없을 경우 자동으로 Publisher 를 종료한다. 따라서 (0) 이 에러나지만,
// autoConnect 는 그냥 인자값 subscriber 수를 만족하면 단순히 connect() 를 호출하므로 인자로 0이 가능하다.
// 다시 말하면 autoConnect 는 subscribe 하는 구독자가 없어도 publisher 를 종료하지 않는다.
// subscriber 여부 관계없이 publisher emit 하고 싶다면 autoConnect(0) 을 사용할 수 있다.
// https://tech.kakao.com/posts/350
fun main() {
    val flux = movie()
//    val flux = movieAutoConnect()

    log.info("no one watching")
    Thread.sleep(2000)

    log.info("Tomas started watching")
    flux
        .take(4)
        .subscribe(Util.newSubscriber("Tomas"))
//
//    Thread.sleep(2000)
//
//    log.info("Jonny started watching")
//    flux
//        .take(3)
//        .subscribe(Util.newSubscriber("Jonny"))
//
//    Thread.sleep(6000)
//
//    log.info("Elden started watching") // re-subscribe
//    flux
//        .take(3)
//        .subscribe(Util.newSubscriber("Elden"))

    Thread.sleep(20000)
}

// cold pub
fun netflix(): Flux<String> {
    return Flux.generate<String?, Int?>(
        { log.info("received the request"); 1 },
        { state, sink ->
            val scene = "movie scene $state"
            log.info("playing $scene")
            sink.next(scene)
            state + 1
        }
    )
//        .take(10)
        .delayElements(Duration.ofSeconds(1))
        .cast(String::class.java)
}

// hot pub
fun movie(): Flux<String> {
    return netflix()
        .share() // cold -> hot
//        .publish().refCount(1)
}

fun movieAutoConnect(): Flux<String> {
    return netflix()
        .publish()
        .autoConnect(0)
}