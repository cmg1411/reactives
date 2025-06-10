package com.kmg.toby.remote

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

const val REMOTE_URI = "http://localhost:8081/remote/service?req="
const val REMOTE_SLOW_URI = "http://localhost:8081/remote/service-slow?req="
const val REMOTE_SLOW2_URI = "http://localhost:8081/remote/service-slow2?req="

@SpringBootApplication
class RemoteApplicationService {

    @RestController
    class RemoteController {
        @GetMapping("/remote/service")
        fun service(req: String): String {
            println("service called")
            return "$req/service"
        }

        @GetMapping("/remote/service-slow")
        fun serviceSlow(req: String): String {
//            throw RuntimeException("error 테스트")
            Thread.sleep(2000)
            return "$req/service-slow"
        }

        @GetMapping("/remote/service-slow2")
        fun serviceSlow2(req: String): String {
            Thread.sleep(2000)
            return "$req/service-slow2"
        }
    }
}

fun main(args: Array<String>) {
    System.setProperty("server.port", "8081")
    System.setProperty("server.tomcat.threads.max", "1000")
    runApplication<RemoteApplicationService>(*args)
}
