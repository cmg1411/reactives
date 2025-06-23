package com.kmg.udemyreactor.common

import com.github.javafaker.Faker
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import java.util.function.UnaryOperator

private val log = LoggerFactory.getLogger("Util")

object Util {
    val faker = Faker.instance()

    fun <T> newSubscriber() = DefaultSubscriber<T>("")
    fun <T> newSubscriber(name: String) = DefaultSubscriber<T>(name)

    fun <T> fluxLogger(name: String): UnaryOperator<Flux<T>> {
        return UnaryOperator { flux ->
            flux.doOnSubscribe { log.info("subscribe $name") }
                .doOnCancel { log.info("cancel: $name") }
                .doOnComplete { log.info("complete: $name") }
        }
    }
}