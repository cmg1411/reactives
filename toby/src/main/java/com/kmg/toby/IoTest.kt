package com.kmg.toby

import kotlinx.coroutines.*

suspend fun main() = coroutineScope {
    println("main start")
    launch {
        newCo()
    }
    println("main end")
}

suspend fun newCo() {
    println("enter newCo")
    delay(1000)
    val num1 = 1
    val num2 = 2
    yield()
    println("newCo: ${num1 + num2}")
}
