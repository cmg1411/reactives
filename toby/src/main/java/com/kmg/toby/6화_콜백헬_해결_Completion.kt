package com.kmg.toby

import org.springframework.http.ResponseEntity
import org.springframework.util.concurrent.ListenableFuture

/**
 * 비동기 처리의 콜백 및 에러 처리를 구조적으로 정의.
 * chainable 한 구조.
 */
open class Completion {
    protected var nextCompletion: Completion? = null

    // 다음 작업을 정의하는 function.
    // function 이기에 리턴값이 있고, 체인을 이어갈 수 있다. 중간 체인으로 사용,
    fun andApply(function: (ResponseEntity<String>) -> ListenableFuture<ResponseEntity<String>>): Completion {
        this.nextCompletion = ApplyCompletion(function = function)
        return nextCompletion!!
    }

    // 다음 작업을 정의하는 consumer.
    // consumer 이기에 리턴값이 없고, 마지막 체인으로 사용.
    fun andAccept(consumer: (ResponseEntity<String>) -> Unit) {
        this.nextCompletion = AcceptCompletion(consumer = consumer)
    }

    // 체이닝 상 andApply 가 1, 2, 3, andError 네개의 체인이 있다 치고
    // 만약 1에서 에러가 나면 2, 3 은 지나치고 andError 에서 처리되는 방법
    fun andError(consumer: (Throwable) -> Unit): Completion {
        this.nextCompletion = ErrorCompletion(errorConsumer = consumer)
        return nextCompletion!!
    }

    // from 으로 생성한 Completion 에서 이 함수를 첫 작업의 성공 callback 으로 등록 했음.
    // 따라서 첫 작업이 끝나면 nextCompletion 의 run 실행.
    fun complete(s: ResponseEntity<String>?) = nextCompletion?.run(s!!)

    // 이게 실행 된다는건 complete 에서 nextCompletion 이 있었고
    // consumer 든 function 이든 여기서 실행.
    open fun run(value: ResponseEntity<String>) {}

    // 객체가 ErrorCompletion 기본적으로 다음 nextCompletion 의 error() 실행.
    // 그대로 던진다는 의미.
    // ErrorCompletion 을 만날 때 까지 넘긴다. ErrorCompletion 는 이 함수를 오버라이드 한다.
    open fun error(e: Throwable) {
        nextCompletion?.error(e)
    }

    companion object {
        fun from(lf: ListenableFuture<ResponseEntity<String>>): Completion {
            val completion = Completion()
            // Completion 에서 정의한 complete(), error() 를 callback 으로 넣는다.
            lf.addCallback(completion::complete, completion::error)

            return completion
        }
    }
}

class ApplyCompletion(
    private val function: ((ResponseEntity<String>) -> ListenableFuture<ResponseEntity<String>>)
) : Completion() {

    override fun run(value: ResponseEntity<String>) {
        function.invoke(value).addCallback(::complete, ::error)
    }
}

class AcceptCompletion(
    private val consumer: ((ResponseEntity<String>) -> Unit)
) : Completion() {

    override fun run(value: ResponseEntity<String>) {
        consumer.invoke(value)
    }
}

class ErrorCompletion(
    private val errorConsumer: ((Throwable) -> Unit)
) : Completion() {

    // ErrorCompletion 에 run() 이 호출되었다는건 이전에 에러가 나지 않았다는 뜻.
    // 그럼 errorComsumer 를 실행하는게 아니라 다음 nextCompletion 로 바로 넘긴다.
    override fun run(value: ResponseEntity<String>) {
        nextCompletion?.run(value)
    }

    override fun error(e: Throwable) {
        errorConsumer.invoke(e)
    }
}
