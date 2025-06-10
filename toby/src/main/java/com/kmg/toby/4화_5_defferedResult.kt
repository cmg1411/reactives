package com.kmg.toby

import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult
import java.util.concurrent.ConcurrentLinkedQueue

private val log = KotlinLogging.logger {}
/**
 * DeferredResult
 *
 * 컨트롤러에서 DeferredResult 를 반환하면, setResult() 를 호출하기 전까지 connection 유지한 채로 응답을 대기한다.
 * 다른 세션에서 setResult() 를 하면 위의 세션의 응답을 그제서야 한다.
 */
@RestController
class `4화_5_deferredResult` {

    private val queue = ConcurrentLinkedQueue<DeferredResult<String>>()

    @GetMapping("/dr")
    fun enqueue(): DeferredResult<String> {
        log.info { "deferred" }
        val dr = DeferredResult<String>()
        queue.add(dr)
        return dr
    }

    @GetMapping("/dr/count")
    fun count() = queue.size.toString()

    @GetMapping("/dr/result")
    fun result(msg: String): String {
        log.info { "setResult" }
        for (dr in queue) {
            dr.setResult("Hello $msg")
            queue.remove(dr)
        }

        return "OK"
    }
}

fun main() {
    /**
     * servlet thread 1 개로 작업. 100개 요청.
     * restTemplate 도 마찬가지로 응답을 지연.
     * /dr/result 호출하는 순간 100개의 restTemplate 가 응답.
     * working 스레드 안뜸
     */
    performanceTest("http://localhost:8080/dr")
}
//
//서블릿 3.1에 추가된 기능
//• ServletInputStream 및 ServletOutputStream에 추가된 새로운 메서드
//• 비동기 처리 또는 업그레이드된 연결 내에서만 사용 가능
//• 비차단 IO로 전환되면 다시 차단 IO로 전환할 수 없습니다
//- ReadListener를 설정하여 읽기 비차단 시작
//• 읽을 데이터가 있을 때 컨테이너가 DataAvailable()을 호출합니다
//• 응용 프로그램은 서블릿 입력 스트림에서 한 번 읽을 수 있습니다
//• 응용 프로그램이 다음 읽기 전에 ServletInputStream#isReady()를 호출해야 합니다
//• 응용 프로그램이 이 작업을 수행하지 않으면 불법 상태 예외가 발생합니다
//- isReady()가 true를 반환하면 응용 프로그램이 ServletInputStream에서 다시 읽을 수 있습니다
//• isReady()가 false를 반환하는 경우 응용 프로그램은 DataAvailable() 콜백 시 다음을 기다려야 합니다
//• 컨테이너는 Ready()가 false()를 반환하고 읽을 데이터가 있는 경우에만 DataAvailable()을 호출합니다
//• 컨테이너는 InputStream의 끝에 도달한 경우에만 AllDataRead()를 호출합니다
//• WriteListener를 설정하여 비차단 쓰기 시작
//• 컨테이너는 데이터를 차단하지 않고 쓸 수 있을 때 WritePossible()을 호출합니다
//• 응용 프로그램이 ServletOutputStream에 한 번 기록할 수 있음
//• 응용 프로그램이 다음 쓰기 전에 ServletOupputStream#isReady()를 호출해야 합니다
//• 응용 프로그램이 이 작업을 수행하지 않으면 불법 상태 예외가 발생합니다
//• isReady()가 true를 반환하면 응용 프로그램이 ServletOutputStream에 다시 쓸 수 있습니다
//• isReady()가 false를 반환하는 경우 응용 프로그램은 WritePossible() 콜백 시 다음을 기다려야 합니다
//• 컨테이너는 Ready()가 false()를 반환하고 차단 없이 데이터를 쓸 수 있는 경우에만 WritePossible()을 호출합니다
//• 타임아웃
//– ServletInputStream 및 ServletOutputStream에만 액세스할 수 있습니다
//– 시간 초과 설정을 위한 API 없음
//– WebSocket 쓰기에 대한 시간 초과 메커니즘을 만들어야 함
//• 나사산 안전성
//– 여행 갈 곳이 많군요
//– 다중 쓰레드를 염두에 두고 쓰기
//– 광범위하게 테스트