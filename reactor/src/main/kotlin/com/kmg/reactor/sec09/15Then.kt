package com.kmg.reactor.sec09

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

private val log = LoggerFactory.getLogger("then")

// then 은 이전 업스트림의 결과가 다 나오면, Mono<Unit> 을 새로운 퍼블리셔로 다운 스트림을 이어서 진행한다.
// 사용 예시는
// 1. then() 으로 save() 동작이 끝날때까지 기다린 후 다운스트림 실행
// -> ex) 디비에 항목을 하나씩 저장할 때 각각 항목마다 어떠한 응답은 주지만 그 응답들은 무시하고, 모두 잘 끝났는지 아닌지만 관심있을 때
// 2. 연선자의 순서를 조정하는 경우
// -> ex) 모든 데이이터를 db 에 저장 완료 한 후, 모든 데이터에 대해 notification 을 보내는 경우
fun main() {
    val records = listOf("record1", "record2", "record3")

    // 1번 case
    saveRecords(records)
        .then()
        .subscribe(Util.newSubscriber())

    // 2번 case
    saveRecords(records)
        .then(sendNotifications(records))
        .subscribe(Util.newSubscriber())
}

fun saveRecords(records: List<String>): Flux<String> {
    return Flux.fromIterable(records)
        .map { "saved : $it" }
        .delayElements(Duration.ofMillis(500))
}

fun sendNotifications(records: List<String>): Mono<Unit> {
    return Mono.fromRunnable { log.info("all records notified : $records") }
}