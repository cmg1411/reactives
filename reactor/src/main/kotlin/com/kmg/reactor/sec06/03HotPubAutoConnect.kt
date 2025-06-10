package com.kmg.udemyreactor.sec06

import com.kmg.udemyreactor.common.Util

fun main() {
    netflix().publish().autoConnect()
        .subscribe(Util.newSubscriber("Tomas"))
}