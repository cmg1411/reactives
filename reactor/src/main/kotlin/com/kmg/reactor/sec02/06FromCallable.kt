package com.kmg.udemyreactor.sec02

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

private val log = LoggerFactory.getLogger("FromCallable")

// Callable 과 Supplier 는 뭔 차이일까.
// 두 FunctionalInterface 의 시그니쳐는 같지만, Callable 에는 throw Exception 이 있다.
// 따라서 throw Exception 이 붙은 메서드를 람다식으로 넣을 경우
// fromCallable 은 try-catch 로 에러를 처리해야하지만
// fromSupplier 는 에러를 처리하지 않아도 된다.
// 다만.. 코틀린을 사용하면 Checked Exception 이 없기 때문에 fromCallable 이나 fromSupplier 가 똑같이 동작한다.
// by chatGPT : Mono.fromCallable은 스레드에서 실행되는 방식이 일부 다를 수 있는데, Reactor의 내부 스케줄링 전략에 따라 Callable은 비동기 작업으로 더 자연스럽게 취급되기도 함.. 그러나 대부분의 일반적인 사용에서는 두 방식이 코틀린에서는 거의 동등하게 동작.
fun main() {
    val list = listOf(1, 2, 3, 4, 5)

    Mono.fromCallable { sum(list) }
        .subscribe(Util.newSubscriber("fromCallable"))

    Mono.fromSupplier { sum(list) }
        .subscribe(Util.newSubscriber("fromCallable"))
}
