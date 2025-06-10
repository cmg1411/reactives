package com.kmg.toby

import com.study.coroutine.remote.REMOTE_SLOW2_URI
import com.study.coroutine.remote.REMOTE_SLOW_URI
import com.study.coroutine.remote.REMOTE_URI
import io.netty.channel.nio.NioEventLoopGroup
import org.springframework.http.ResponseEntity
import org.springframework.http.client.Netty4ClientHttpRequestFactory
import org.springframework.util.StopWatch
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.AsyncRestTemplate
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.request.async.DeferredResult
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@RestController
class `5화_1` {

    private val restTemplate = RestTemplate()
    private val asyncRestTemplate = AsyncRestTemplate()
    private val netty = AsyncRestTemplate(Netty4ClientHttpRequestFactory(NioEventLoopGroup(1)))

    @GetMapping("/callable2")
    fun directReturn(idx: Int): String {
        return "rest $idx"
    }

    @GetMapping("/remote/test")
    fun returnAfterApiCall(idx: Int): String {
        val res = restTemplate.getForObject("${REMOTE_URI}hello$idx", String::class.java)
        return res + idx
    }

    @GetMapping("/remote/test-slow")
    fun returnAfterSlowApiCall(idx: Int): String {
        val res = restTemplate.getForObject("${REMOTE_SLOW_URI}hello$idx", String::class.java)
        return res + idx
    }

    /**
     * AsyncRestTemplate 은 Spring 5 에서 삭제되어서 스프링 부트 2점대로 내렸음.
     *
     * spring 에서는 Controller 가 ListenableFuture 를 반납하면
     * 서블릿 스레드 자체는 바로 리턴되고
     * 비동기의 응답에 대한 콜백을 스프링이 만들어주기 때문에, 응답을 받을 수 있다.
     */
    @GetMapping("/remote/test-slow/asyncresttemplate")
    fun returnAfterSlowApiCallWithAsyncRestTemplate(idx: Int): ListenableFuture<ResponseEntity<String>> {
        return asyncRestTemplate.getForEntity("${REMOTE_SLOW_URI}=hello$idx", String::class.java)
    }

    @GetMapping("/remote/test-slow/netty")
    fun returnAfterSlowApiCallWithNetty(idx: Int): ListenableFuture<ResponseEntity<String>> {
        return netty.getForEntity("${REMOTE_SLOW_URI}=hello$idx", String::class.java)
    }

    @GetMapping("/remote/test-slow/netty-with-callback")
    fun returnAfterSlowApiCallWithNettyWithCallback(idx: Int): DeferredResult<String> {
        // 콜백은 단순 실행하고 마는 것이다. 값을 리턴하거나 하지 않는다.
        // 콜백함수의 리턴값을 스프링 컨트롤러에서 리턴하는 방법으로 DeferredResult 를 사용한다.
        // 콜백 함수의 리턴값을 deferredResult 에 저장해놓고, deferredResult 를 컨트롤러 리턴을 한다.
        val dr = DeferredResult<String>()

        val listenableFuture = netty.getForEntity("${REMOTE_SLOW_URI}hello$idx", String::class.java)
        listenableFuture.addCallback(
            { dr.setResult(it!!.body!! + "/deferred") },
            { dr.setErrorResult(it) }
        )

        return dr
    }

    @GetMapping("/remote/test-slow/netty-with-apicall-callback")
    fun returnAfterSlowApiCallWithNettyWithApiCallCallback(idx: Int): DeferredResult<String> {
        // 콜백은 단순 실행하고 마는 것이다. 값을 리턴하거나 하지 않는다.
        // 콜백함수의 리턴값을 스프링 컨트롤러에서 리턴하는 방법으로 DeferredResult 를 사용한다.
        // 콜백 함수의 리턴값을 deferredResult 에 저장해놓고, deferredResult 를 컨트롤러 리턴을 한다.
        val dr = DeferredResult<String>()

        val listenableFuture = netty.getForEntity("${REMOTE_SLOW_URI}hello$idx", String::class.java)
        listenableFuture.addCallback(
            {
                // 다른 API 요청을 콜백으로 등록한다.
                // 첫번째 API 비동기 요청이 끝나면 두번째 API 요청을 하게 됨.
                val listenableFuture2 = netty.getForEntity("${REMOTE_SLOW2_URI}hello$idx", String::class.java)
                listenableFuture2.addCallback(
                    { dr.setResult(it!!.body!! + "/deferred") },
                    { dr.setErrorResult(it) }
                )
            },
            { dr.setErrorResult(it) }
        )

        return dr
    }

    /**
     * Mono 를 그래돌 리턴하면 된다.
     * 콜백으로 응답을 반환하는 코드를 스프링에서 만들어 준다.
     */
    @GetMapping("/remote/test-slow/webclient")
    fun returnAfterSlowApiCallWithWebClient(idx: Int): Mono<String> {

        return WebClient.create()
            .get()
            .uri("${REMOTE_SLOW_URI}=hello$idx")
            .retrieve()
            .bodyToMono(String::class.java)
    }
}

/**
 * 이 서버의 톰캣 스레드는 1개라고 가정.
 */
fun main() {
    /**
     * 일반 rest api 호출
     * 0.175450125
     * 순서가 연속적이지 않고 뒤죽박죽. barrier 가 잘 동작했다.
     * 그냥 단순 빠르게 리턴하는 컨트롤러니 빨리 끝남.
     */
//    performanceTestRefactored("http://localhost:8080/callable2?idx={idx}")

    /**
     * 외부 서버에 API 호출 이 있는 경우
     * 외부 서버는 tomcat thread 1000 개
     * 0.3057695
     * 어짜피 외부 서버도 빠르게 응답을 주기에 별 문제가 없어보임.
     */
//    performanceTestRefactored("http://localhost:8080/remote/test?idx={idx}")

    /**
     * 외부 서버 API 호출은 Blocking Call 이며 2초 걸림.
     * 이 서버의 스레드는 1개이기 때문에, 2초동안 1개의 스레드가 아무것도 못하니 애플리케이션이 아무것도 못함.
     * 결국 100개의 요청은 2초 텀으로 하나씩 와서 결국 200초가 걸림.
     * --> 외부 API 호출한 2초동안 이 서버의 스레드는 Blocking 되어 아무것도 못하니, 비동기 IO 로 바꿔버리자!!
     */
//    performanceTestRefactored("http://localhost:8080/remote/test-slow?idx={idx}")

    /**
     * 2.189
     * 비동기이다. 논블로킹이다.
     * But. AsyncRestTemplate 로 비동기 요청을 100개 보내면 내부적으로 100개의 스레드를 만들어서 처리한다.
     * 물론 그 100개의 스레드는 요청을 보내고 바로 없어지기에 100개의 스레느는 일순간 생겼다가 사라진다.
     * (100 개의 스레드가 block 이 걸리는건 아님) (비동기긴 하다)
     * 이러면 사실 서버 자원 입장으로는 서블릿 스레드 100개 띄우는거랑 (보단 덜하지만) 비슷한 문제가 있어서 Deprecated 된 후 삭제 ..
     */
//    performanceTestRefactored("http://localhost:8080/remote/test-slow/asyncresttemplate?idx={idx}")

    /**
     * 우리가 원하는 비동기 IO (자원을 적게쓰는)
     * 2.386350834
     * visualVm 으로 확인했을때 스레드가 많이 뜨지 않음.
     * 비동기 IO 를 던지기 위해 스레드 약 1개만 더 만들어짐.
     * (우리가 이벤트 그룹 갯수를 1로 지정했으니) (요청은 100개지만) (이름은 nioEventLoopGroup-2-1)
     * netty 는 기본적으로 코어수 * 2 개의 스레드를 띄운다. (이번 예에서는 1개로 지정했다)
     */
//    performanceTestRefactored("http://localhost:8080/remote/test-slow/netty?idx={idx}")

    /**
     * 2.348344042
     * 비동기 IO 응답에 콜백을 만드는 방법.
     * 콜백은 따로 리턴하는 기능이 없으니, 스프링 컨트롤러 응답을 위해 DeferredResult 를 사용한다.
     */
//    performanceTestRefactored("http://localhost:8080/remote/test-slow/netty-with-callback?idx={idx}")

    /**
     * 4.412962792
     * 비동기 API 콜의 콜백으로 API 콜을 등록했다. 실제로 총 200개의 요청이 나간 셈. 해서 정확히 4초 조금 더 걸렸음.
     * 여전히 스레드는 적게 쓴다.
     * 콜백 작업도 nioEventLoopGroup-2-1 에서 처리됨.
     */
    performanceTestRefactored("http://localhost:8080/remote/test-slow/netty-with-apicall-callback?idx={idx}")

    /**
     * 2.109452375
     * visualVm 으로 확인했을때 스레드가 많이 뜨지 않음.
     * 비동기 IO 를 던지기 위해 스레드 10개만 더 만들어짐.(요청은 100개지만) (이름은 reactor-http-kqueue-1)
     * 적은 수의 스레드로 비동기 논블로킹으로 IO 를 실행했음
     */
//    performanceTestRefactored("http://localhost:8080/remote/test-slow/webclient?idx={idx}")
}


fun performanceTestRefactored(url: String) {
    val counter = AtomicInteger()
    val es = Executors.newFixedThreadPool(100)

    val restTemplate = RestTemplate()

    /**
     * 자바에서의 스레드 동기화 : CyclicBarrier
     * N 개의 스레드가 모두 await() 을 호출할 때 까지 모든 스레드를 await() 시점에서 멈춰놓고
     * N 개의 스레드가 모두 await() 을 호출하면 일제히 다음 작업 진행.
     */
    val barrier = CyclicBarrier(101)

    (1..100).forEach {
        es.execute {
            val idx = counter.addAndGet(1)

            barrier.await() // 100개

            val sw = StopWatch() // A
            sw.start()
            val result = restTemplate.getForObject(url, String::class.java, idx)
            sw.stop()

            log.info { "Elapsed $idx : ${sw.totalTimeSeconds} / $result" }
        }
    }

    barrier.await() // 101번째

    val main = StopWatch() // 100 개 의 A 시점과 여기까지 101 개가 동시에 실행
    main.start()
    es.shutdown()
    es.awaitTermination(100, TimeUnit.SECONDS)
    main.stop()
    log.info { "Total : ${main.totalTimeSeconds}" }
}

/**
 * [메모리]
 * 애초에 스레드를 적게 띄우니 메모리를 적게 씀.
 * 비동기 프로그래밍을 잘 짜면 메모리를 굉장히 절약 가능.
 */
