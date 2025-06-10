package com.kmg.toby

import mu.KotlinLogging
import org.springframework.util.StopWatch
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private val log = KotlinLogging.logger {}

@RestController
class `4화_servlet_3` {

    /**
     * 1. [servlet thread] callable 찍힘
     * 2. [worker thread] World 찍힘
     * 3. 메서드 끝남
     * 4. 2초 뒤 웹에 Hello 찍힘
     */
    @GetMapping("/callable")
    fun callable(): Callable<String> {
        log.info { "callable" } // servlet thread : nio-8080-exec-1

        return Callable {
            log.info { "World" } // worker thread
            Thread.sleep(7000)
            "Hello"
        }
    }

    @GetMapping("/block")
    fun block(): String {
        log.info { "World" }
        Thread.sleep(2000)
        return "Hello"
    }
}

/**
 * test case
 * 1. /block server.tomcat.threads.max=200 => 2.182038958
 * 2. /block server.tomcat.threads.max=20 => 10.204122916 // 20개씩 100개. 한번에 2초니 총 10초
 * 3. /callable server.tomcat.threads.max=200 => 2.196016625
 * 4. /callable server.tomcat.threads.max=20 => 2.218295875
 */
fun main() {
    performanceTest("http://localhost:8080/callable")
//    performanceTest("http://localhost:8080/block")
}

fun performanceTest(url: String) {
    val counter = AtomicInteger()
    val es = Executors.newFixedThreadPool(300)

    val restTemplate = RestTemplate()

    val main = StopWatch()
    main.start()

    (1..300).forEach {
        es.execute {
            val idx = counter.addAndGet(1)
            log.info { "Thread $idx" }

            val sw = StopWatch()
            sw.start()

            val result = restTemplate.getForObject(url, String::class.java)
            log.info { "Result $idx : $result" }

            sw.stop()
            log.info { "Elapsed $idx : ${sw.totalTimeSeconds}" }
        }
    }

    es.shutdown()
    es.awaitTermination(100, TimeUnit.SECONDS)

    main.stop()
    log.info { "Total : ${main.totalTimeSeconds}" }
}

//Feature added in Servlet 3.1
//• New methods added to ServletInputStream and ServletOutputStream
//• May only be used within asynchronous processing or upgraded connections
//• Once switched to non-blocking IO it is not permitted to switch back to blocking IO
//- Start non-blocking read by setting the ReadListener
//• Container will call onDataAvailable() when there is data to read
//• Application may read once from the ServletInputStream
//• Application must call ServletInputStream#isReady() before next read
//• An IllegalStateException is thrown if applications don’t do this
//- If isReady() returns true, the application may read again from the ServletInputStream
//• If isReady() returns false, the application must wait for the next onDataAvailable() callback
//• The container will only call onDataAvailable() once isReady() has returned false and there is data to read
//• The container will only call onAllDataRead() when the end of the InputStream is reached
//• Start non-blocking write by setting the WriteListener
//• Container will call onWritePossible() when data can be written without blocking
//• Application may write once to the ServletOutputStream
//• Application must call ServletOuputStream#isReady() before next write
//• An IllegalStateException is thrown if applications don’t do this
//• If isReady() returns true, the application may write again to the ServletOutputStream
//• If isReady() returns false, the application must wait for the next onWritePossible() callback
//• The container will only call onWritePossible() once isReady() has returned false and data may be written without blocking
//• Timeouts
//– Only have access to the ServletInputStream and ServletOutputStream
//– No API for setting timeouts
//– Had to create a timeout mechanism for WebSocket writes
//• Thread safety
//– Lots of places to trip up
//– Write with multi-threading in mind
//– Test extensively