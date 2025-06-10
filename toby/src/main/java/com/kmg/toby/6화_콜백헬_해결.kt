package com.kmg.toby

import com.study.coroutine.remote.REMOTE_SLOW2_URI
import com.study.coroutine.remote.REMOTE_SLOW_URI
import io.netty.channel.nio.NioEventLoopGroup
import org.springframework.http.client.Netty4ClientHttpRequestFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.AsyncRestTemplate
import org.springframework.web.context.request.async.DeferredResult

/**
 * CompletableFuture 의 대략적인 구조를 직접 짜보자.
 *
 * 5화의 ListenableFuture 의 callback 을 중첩하는 구조는 문제가 있다.
 * [근본적 문제] : 비동기 처리기 때문에 바로 결과를 받지 못하고, ListenableFuture 같은 매개체를 통해 받아야 한다.
 * 그리고 콜백을 사용해서 다음 동작을 정의해야 한다.
 * 하지만 ListenableFuture 과 그 안의 callback 은 단지 실행하는거지, 어떤 작업이 끝나면 다른 작업을 한다던가의 구조적인 표현은 가지지 못한다.
 *  1. callback 을 중첩하는 구조는 가독성이 떨어진다. (콜백헬)
 *  2. 같은 에러 처리 코드를 매번 서줘야 하므로 중복이 된다.
 *
 *
 *  이를 해결하기 위해 다음 작업을 해보자.
 *  1. 체이닝의 마지막 작업. 비동기 처리의 결과를 받아서 처리(리턴 없이) 하는 andAccept()
 *  2. 체이닝의 중간 작업. 비동기 처리의 결과를 받아서 비동기 요청을 하는 andApply()
 *  3. 체이닝 중 에러가 발생하면 처리해는 andError()
 *  4.
 */
@RestController
class `6화_콜백헬_해결` {

    private val rt = AsyncRestTemplate(Netty4ClientHttpRequestFactory(NioEventLoopGroup(1)))

    @GetMapping("/remote/completion")
    fun resolveCallbackHell(idx: String): DeferredResult<String> {
        val deferredResult = DeferredResult<String>()

        Completion.from(rt.getForEntity("${REMOTE_SLOW_URI}${idx}", String::class.java))
            .andApply { rt.getForEntity("${REMOTE_SLOW2_URI}${it.body}", String::class.java, it.body!!) }
            .andError { deferredResult.setErrorResult(it.message!!) } // message 나 toString() 하지 않으면 클라이언트에서도 500 에러 뱉어버림.
            .andAccept { deferredResult.setResult(it.body!!) }

        return deferredResult
    }
}


/**
 * Error 테스트는 두가지 본다.
 * 1. 에러가 안나면 무시되는 것 테스트 : andError{} 넣고 성공 동작 확인
 * 2. 이러가 나면 뒤에 API 무시되고 에러 찍는 것 : 첫번재 remote API 에서 Exception throw 하도록 한다. 그러면 실행 시간이 0.3초 정도로 빠르게 실행되어야 함. (2초 delay API 하나도 안타니.)
 */
fun main() {
    performanceTestRefactored("http://localhost:8080/remote/completion?idx={idx}")
}
