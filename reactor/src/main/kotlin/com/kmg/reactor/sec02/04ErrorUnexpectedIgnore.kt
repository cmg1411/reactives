package com.kmg.udemyreactor.sec02

import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("ErrorUnexpectedIgnore")

fun main() {
    // LambdaMonoSubscriber 의 doOnError() 참고
    // Publisher 가 에러를 던졌는데 errorHandler 가 정의되어있지 않으면, Operators.onErrorDropped 를 실행한다.
    getUsername(3).subscribe { log.info("received : $it") }
}