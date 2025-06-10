package com.kmg.toby

import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DelayController {

    @PostMapping("/delay")
    suspend fun delayServer(): String {
//        val res = WebClient.create("http://localhost:8081/remote/service?req=$123")
//            .get()
//            .retrieve()
//            .bodyToFlow<String>()
        delay(1500)
//        return res.first()
        return "res.first()"
    }
}

data class DataDto(
    val name: String,
)