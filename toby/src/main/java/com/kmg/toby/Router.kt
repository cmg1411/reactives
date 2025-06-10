package com.kmg.toby

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

class Router {
}

@Component
class CategoryRouter {

    @Bean
    fun coRoute(@Autowired handler: CategoryHandler) = coRouter {
        (accept(MediaType.APPLICATION_JSON) and "/dsl-coroutine").nest {
            GET("/echo/{number}").invoke(handler::getCategories)
        }
    }
}

@Component
class CategoryHandler {

    suspend fun getCategories(request: ServerRequest): ServerResponse {
        val number = request.pathVariable("number").toInt()
        return ServerResponse.ok().bodyValueAndAwait(number)
    }
}


