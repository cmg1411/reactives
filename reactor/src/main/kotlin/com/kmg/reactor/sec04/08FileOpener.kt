package com.kmg.udemyreactor.sec04

import com.kmg.udemyreactor.common.Util
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.SynchronousSink
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path

fun main() {
    FileReaderService()
        .read(Path.of("/Users/tomas/Documents/udemy-reactor/src/main/kotlin/com/kmg/udemyreactor/sec04/files/document"))
        .subscribe(Util.newSubscriber())
}

class FileReaderService {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun read(path: Path): Flux<String> {
        return Flux.generate(
            { openFile(path) },
            { state, sink -> readFile(state, sink) },
            { closeFile(it) },
        )
    }

    private fun openFile(path: Path): BufferedReader {
        log.info("open file : $path")
        return Files.newBufferedReader(path)
    }

    private fun readFile(reader: BufferedReader, sink: SynchronousSink<String>): BufferedReader {
        val line = reader.readLine()
        log.info("read line : $line")
        if (line == null) {
            sink.complete()
        } else {
            sink.next(line)
        }
        return reader
    }

    private fun closeFile(reader: BufferedReader) {
        log.info("close file : $reader")
        reader.close()
    }
}
