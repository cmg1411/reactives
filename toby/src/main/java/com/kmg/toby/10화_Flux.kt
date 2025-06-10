package com.kmg.toby

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink
import java.time.Duration
import java.util.function.BiFunction
import java.util.stream.Stream

@RestController
class `10화_Flux` {

    @GetMapping("/10-mono")
    fun monoEvent() = Mono.just(Event(1, "event1"))

    @GetMapping("/10-list-mono")
    fun listMonoEvent(): Mono<List<Event>> {
        return Mono.just(listOf(Event(1, "event1"), Event(2, "event2")))
    }

    /**
     * [호출 결과]
     * [
     *   {
     *     "id": 1,
     *     "message": "event1"
     *   },
     *   {
     *     "id": 2,
     *     "message": "event2"
     *   }
     * ]
     *
     * Mono 와 비교해서,
     * Flux 는 [] 안에 (배열 안에) 담겨 있다.
     *
     * 위의 Mono<List<Event>> 를 리턴하는 listMonoEvent() 와 결과가 같다.
     *
     * [Mono<List<Event>> vs Flux<Event>
     * 1. Mono<List<Event>> 는 map 같은 연산을 할 때 List 전체에 대해 연산 하는 것 이지만,
     * Flux<Event> 는 안의 각각의 요소들에 대해 연산을 하는 것.
     *
     * 2. Flux 는 stream 처리가 가능.
     */
    @GetMapping("/flux")
    fun fluxEvent(): Flux<Event> {
        val iter = listOf(Event(1, "event1"), Event(2, "event2"))
        return Flux.fromIterable(iter)
//        return Flux.just(Event(1, "event1"), Event(2, "event2"))
    }

    /**
     * ResponseBodyEmitter 와 같은 것이다. (4화_6_emitter.kt 참고)
     *
     * [스트림 호출 결과] : 서버쪽의 이벤트에 따라 데이터가 쪼개져서 들어옴
     * data:{"id":1,"message":"event1"}
     * data:{"id":2,"message":"event2"}
     */
    @GetMapping(value = ["/flux-stream"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun fluxStreamEvent(): Flux<Event> {
        val iter = listOf(Event(1, "event1"), Event(2, "event2"))
        return Flux.fromIterable(iter)
    }

    /**
     * take -> subscriber 의 request 에서 최대 몇개의 데이터를 받을지 정할 수 있다.
     *      -> cancel() 을 통해 취소해버린다.
     */
    @GetMapping(value = ["/flux-stream-generator"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun fluxStreamGeneratorEvent(): Flux<Event> {
        val stream = Stream.generate { Event(System.currentTimeMillis(), "event") }

        return Flux.fromStream(stream)
            .delayElements(Duration.ofSeconds(1)) // delay 를 처리하는 backGround Thread 가 동작.
    }

    /**
     * Flux.generate() 는 SynchronousSink 가 리턴타입인 Consumer | Supplier 받는다.
     * SynchronousSink 는 next 를 통해 다음 emitter(스트림 응답) 을 셋팅한다.
     */
    @GetMapping(value = ["/flux-stream-generator2"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun fluxStreamGenerator2Event(): Flux<Event> {
        return Flux.generate<Event> { sink ->
            sink.next(Event(System.currentTimeMillis(), "event")) // 다음 데이터를 생성하는 코드
        }.delayElements(Duration.ofSeconds(1))
            .take(10)
    }

    /**
     * 초기값, generator 시 id 를 사용하는 법.
     * public static <T, S> Flux<T> generate(Callable<S> stateSupplier, BiFunction<S, SynchronousSink<T>, S> generator)
     */
    @GetMapping(value = ["/flux-stream-generate-range"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun fluxStreamGenerateRangeEvent(): Flux<Event> {

        return generateWithState.delayElements(Duration.ofSeconds(1))
            .take(10)
    }

    /**
     * [ zip]
     * 두 FLUX 흐름을 합쳐서 하나의 FLUX 로 만든다.
     * 두 FLUX 를 하나 하나씩 순차적으로 가져온다.
     *
     * 둘중 하나의 FLUX 가 delay FLUX 라면 지연하는 효과를 만들 수 있다.
     *
     * 용도 : 데이터를 다른 곳에서 가져와야 하는 경우
     *  -> 순서만 맞추면 각 데이터를 가져오는걸 따로 정의하면 된다.
     *  -> 그 이후 zip 으로 조합해서 사용하면 된다.
     *  -> 데이터를 가져오는 각각을 스트림으로 만들고 zip 으로 조합하면 어디가 먼저 오든 간에 둘 다 도착하면 Client 로 반환하는 말그대로 stream 형태로 조합 응답 구조를 만들 수 있다.
     *
     * [ interval]
     * 데이터 stream 에 간격을 만들 때 사용. delay 와 같음.
     */
    @GetMapping(value = ["/flux-zip"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun fluxZip(): Flux<Event> {

        return Flux.zip(generateWithState, intervalFlux)
            .map { it.t1 }
            .take(10)
    }

    @GetMapping(value = ["/flux-zip2"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun fluxZip2(): Flux<Event> {

        val generator = Flux.generate { it.next("value") }

        return Flux.zip(generator, intervalFlux)
            .map { Event(it.t2, it.t1) }
            .take(10)
    }
}

private val generateWithState = Flux.generate(
    { 1L }, // 초기값 supplier
    BiFunction<Long, SynchronousSink<Event>, Long> { id, sink -> // 현재 state 와 sink 받아서 다음 state 를 리턴하는 BiFunction
        sink.next(Event(id, "event$id"))
        return@BiFunction (id + 1)
    }
)

private val intervalFlux = Flux.interval(Duration.ofSeconds(1))

class Event(val id: Long, val message: String)
