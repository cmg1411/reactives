package com.kmg.udemyreactor.common

import com.github.javafaker.Faker

object Util {
    val faker = Faker.instance()

    fun <T> newSubscriber() = DefaultSubscriber<T>("")
    fun <T> newSubscriber(name: String) = DefaultSubscriber<T>(name)
}