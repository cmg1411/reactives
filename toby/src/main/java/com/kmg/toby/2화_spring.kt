package com.kmg.toby

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Flow.*

@RestController
class `2화_spring` {

    /**
     * Publisher 를 리턴하면
     * Spring 이 자동으로 subscriber 를 만들어서 Publisher 에 등록한다.
     *
     * subscription 의 request 에서 호출하는 onNext() 의 데이터가 컨트롤러에서 리턴된다.
     * (자동으로 만들어진 subscriber 의 onSubscribe() 안에서 subscription 의 request 를 호출하나보다.)
     */
    @GetMapping("/hello")
    fun hello(name: String) = Publisher {
        it.onSubscribe(object : Subscription {
            override fun request(n: Long) {
                it.onNext("Hello $name")
                it.onComplete()
            }

            override fun cancel() {}
        })
    }
}
