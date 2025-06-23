package com.kmg.reactor.sec10

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

// 각 윈도우 그룹 마다 별도의 파일에 작성 하는 예제
fun main() {
    val path = "reactor/src/main/resources/sec10/file%d.txt"
    val counter = AtomicInteger(0)

    Flux.interval(Duration.ofMillis(500))
        .map { "Event $it" }
        .window(Duration.ofMillis(1800))
        .flatMap {
            FileWriter.create(it, Path.of(String.format(path, counter.incrementAndGet())))
        }
        .subscribe()

    Thread.sleep(10000)
}

class FileWriter(
    val path: Path,
) {

    private lateinit var writer: BufferedWriter

    private fun createFile() {
        this.writer = Files.newBufferedWriter(path)
    }

    private fun closeFile() {
        if (::writer.isInitialized) {
            writer.close()
        }
    }

    fun write(line: String) {
        writer.write(line)
        writer.newLine()
        writer.flush()
    }

    companion object {
        fun create(flux: Flux<String>, path: Path): Mono<Void> {
            val writer = FileWriter(path)
            return flux.doOnNext { writer.write(it) }
                .doFirst { writer.createFile() }
                .doFinally { writer.closeFile() }
                .then()
        }
    }
}