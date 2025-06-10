package com.kmg.toby//package com.study.coroutine.reactive
//
//import org.springframework.util.concurrent.ListenableFuture
//
//open class Completion<S,T> {
//    protected var nextCompletion: Completion<out Any?,out Any?>? = null
//
//    fun <V> andApply(function: (T) -> ListenableFuture<V>): Completion<T,V> {
//        this.nextCompletion = ApplyCompletion(function = function)
//        return (nextCompletion as ApplyCompletion<T, V>)!!
//    }
//
//    fun andAccept(consumer: (T) -> Unit) {
//        this.nextCompletion = AcceptCompletion<T,Void>(consumer = consumer)
//    }
//
//    fun andError(consumer: (Throwable) -> Unit): Completion<T,T> {
//        this.nextCompletion = ErrorCompletion<T>(errorConsumer = consumer)
//        return (nextCompletion as ErrorCompletion<T>)!!
//    }
//
//    // complete 는 비동기 실행 후 반환시 실행이니 T
//    fun complete(s: T?) = nextCompletion?.run(s!!)
//
//    // (complete 와 비교해서) run 은 nextCompletion 이 실행 시키는 것이니 S
//    open fun run(value: S) {}
//
//    open fun error(e: Throwable) {
//        nextCompletion?.error(e)
//    }
//
//    companion object {
//        fun <S,T> from(lf: ListenableFuture<T>): Completion<S,T> {
//            val completion = Completion<S,T>()
//            lf.addCallback(completion::complete, completion::error)
//
//            return completion
//        }
//    }
//}
//
//class ApplyCompletion<S,T>(
//    private val function: ((S) -> ListenableFuture<T>)
//) : Completion<S,T>() {
//
//    override fun run(value: S) {
//        function.invoke(value).addCallback(::complete, ::error)
//    }
//}
//
//class AcceptCompletion<S,T>(
//    private val consumer: ((S) -> Unit)
//) : Completion<S,T>() {
//
//    override fun run(value: S) {
//        consumer.invoke(value)
//    }
//}
//
//class ErrorCompletion<T>(
//    private val errorConsumer: ((Throwable) -> Unit)
//) : Completion<T,T>() {
//
//    override fun run(value: T) {
//        nextCompletion?.run(value)
//    }
//
//    override fun error(e: Throwable) {
//        errorConsumer.invoke(e)
//    }
//}
