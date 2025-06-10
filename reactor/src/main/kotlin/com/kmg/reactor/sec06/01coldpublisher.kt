package com.kmg.udemyreactor.sec06

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

private val log = LoggerFactory.getLogger("01coldpublisher")

fun main() {
//    case1()
    case2()
}

// cold publisher 이기 때문에, 매 subscribe 마다 create 를 새로 실행한다.
// 두 subscribe 모두 0,1,2,3 을 받는다.
// flux 외부에서 atomicInteger 같은 공유 변수를 쓰면 두번째 구독자가 첫번째 구동자가 받은 값 이후를 이어서 받을 수 있다.
fun case1() {
    val flux = Flux.create { fluxSink ->
        log.info("invoked")
        for(i in 0..3) {
            log.info("next: $i")
            fluxSink.next(i)
        }
        fluxSink.complete()
    }

    flux.subscribe(Util.newSubscriber())
    flux.subscribe(Util.newSubscriber())
}

// 이 경우 첫번째 구독자는 무시된다.
// cold publisher 이기 때문에, 매 subscribe 마다 create 를 실행하게 되며,
// 외부변수 sink 의 객체를 갱신한다.
// 마지막 next 를 실행하는 sink 의 경우 두번째 subscribe 의 create 에서 생성된 fluxSink 이기 때문에, 첫번째 subscriber 는 데이터를 받지 못한다.
fun case2() {
    var sink: FluxSink<Any>? = null
    var sink2: FluxSink<Any>? = null
    val flux = Flux.create { fluxSink ->
        log.info("invoked")
        sink = fluxSink
    }

    flux.subscribe(Util.newSubscriber("1"))
    sink2 = sink // 첫번째 구독자를 사용하려면 이렇게 다른 변수에 집어넣어준 후 사용해야함.
    log.info("sink is : ${sink!!.javaClass.name}@${System.identityHashCode(sink)}") // 1
    flux.subscribe(Util.newSubscriber("2"))
    log.info("sink is : ${sink!!.javaClass.name}@${System.identityHashCode(sink)}") // 2, 1과 2는 다른 객체임.

    sink?.next("hello1")
    sink?.next("hello2")
    sink?.next("hello3")

//    sink2?.next("hello1")
//    sink2?.next("hello2")
//    sink2?.next("hello3")
}