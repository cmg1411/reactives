package com.kmg.reactor.sec10

import com.kmg.udemyreactor.common.Util
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.function.Predicate
import java.util.function.UnaryOperator

// GroupBy 의 예시. 그룹마다 적용해야할 연산자를 정의하고,
// 그룹을 나눠서 각 그룹에 맞는 연산자를 적용하는 디자인 패턴이 가능함.
fun main() {
    orderStream()
        .filter(OrderProcessingService.canProcess())
        .groupBy(PurchaseOrder::category)
        .flatMap { it.transform(OrderProcessingService.getProcessor(it.key())!!) }
        .subscribe(Util.newSubscriber())

    Thread.sleep(60000)
}

private fun orderStream(): Flux<PurchaseOrder> {
    return Flux.interval(Duration.ofMillis(500))
        .map { PurchaseOrder.create() }
}

class OrderProcessingService {

    companion object {
        private val PROCESSOR_MAP = mapOf(
            "Kids" to kidsProcessing(),
            "Automotive" to automotiveProcessing(),
        )

        private fun automotiveProcessing(): UnaryOperator<Flux<PurchaseOrder>> {
            return UnaryOperator<Flux<PurchaseOrder>> {
                it.map { order -> PurchaseOrder(order.item, order.category, order.price) }
            }
        }

        private fun kidsProcessing(): UnaryOperator<Flux<PurchaseOrder>> {
            return UnaryOperator<Flux<PurchaseOrder>> {
                it.flatMap { order -> getFreeKidsOrder(order).flux().startWith(order) }
            }
        }

        private fun getFreeKidsOrder(order: PurchaseOrder): Mono<PurchaseOrder> {
            return Mono.fromSupplier {
                PurchaseOrder(
                    "${order.item} - Free Kids Item",
                    order.category,
                    0
                )
            }
        }

        fun canProcess(): Predicate<PurchaseOrder> {
            return Predicate<PurchaseOrder> { PROCESSOR_MAP.containsKey(it.category) }
        }

        fun getProcessor(category: String): UnaryOperator<Flux<PurchaseOrder>>? {
            return PROCESSOR_MAP[category]
        }
    }
}

data class PurchaseOrder(
    val item: String,
    val category: String,
    val price: Int,
) {
    companion object {
        fun create(): PurchaseOrder {
            return PurchaseOrder(
                Util.faker.commerce().productName(),
                Util.faker.commerce().department(),
                Util.faker.random().nextInt(10, 100),
            )
        }
    }
}