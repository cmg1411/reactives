package com.kmg.udemyreactor.sec02

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

private val log = LoggerFactory.getLogger("FromSupplier")

fun main() {
    val list = listOf(1, 2, 3, 4, 5)

    // just 는 메모리에 있는 값을 방출하는 것이므로 sum(list) 를 호출해야 한다. 문제는 subscriber 가 없어도 호출한다.
    // 이를 방지하기 위해 fromSupplier 를 사용한다. lazy 하게 subscriber 가 Request 했을 때 값을 만든다.
    // just { } 도 근데 람다식이라 fromSupplier 처럼 동작한다고 착각하면 안됨. 저건 진짜 람다식을 스트림으로 흘려보낸다.
        // request 시 () -> kotlin.Int 이게 나온다는 말이다.
    Mono.just(sum(list))
//        .subscribe(Util.newSubscriber("just"))
    Mono.just { sum(list) }
//        .subscribe(Util.newSubscriber("just lambda"))
    Mono.fromSupplier { sum(list) }
//        .subscribe(Util.newSubscriber("fromSupplier"))
}

@Throws(Exception::class)
fun sum(list: List<Int>): Int {
    log.info("finding the sum of $list")
    return list.sum()
}