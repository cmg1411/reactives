package com.study.coroutine.reactive.config

import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFuture

private val log = KotlinLogging.logger {}

@Service
class MyService {
    @Async("myThreadPool")
    fun workAsync(req: String): ListenableFuture<String> {
        log.info { "${Thread.currentThread()} Work" }
        return AsyncResult("$req/asyncWork")
    }

    fun work(req: String): String {
        log.info { "${Thread.currentThread()} Work" }
        return "$req/asyncWork"
    }
}

@Configuration
class Config {
    @Bean
    fun myThreadPool() = ThreadPoolTaskExecutor().apply {
        corePoolSize = 1
        maxPoolSize = 1
        initialize()
    }
}
