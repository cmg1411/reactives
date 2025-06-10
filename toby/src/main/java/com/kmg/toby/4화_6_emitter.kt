package com.kmg.toby

import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
import java.util.concurrent.Executors

private val log = KotlinLogging.logger {}
/**
 * http 에서 결과를 모아서 쏘는게 아니라
 * sse 방식을 이용해서 streaming 방식으로 응답.
 */
@RestController
class `4화_6_emitter` {

    // http://localhost:8080/emitter
    @GetMapping("/emitter")
    fun emitter(): ResponseBodyEmitter {
        val emitter = ResponseBodyEmitter()

        Executors.newSingleThreadExecutor().submit {
            for (i in 1..50) {
                emitter.send("<p>Stream $i</p>")
                Thread.sleep(200)
            }
        }

        return emitter
    }
}