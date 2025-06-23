plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.kmg"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.projectreactor:reactor-bom:2024.0.5"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
//    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("io.projectreactor:reactor-core")
    implementation("io.projectreactor.netty:reactor-netty-core")
    implementation("io.projectreactor.netty:reactor-netty-http")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    testImplementation("io.projectreactor:reactor-test")

    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("com.github.javafaker:javafaker:1.0.2")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
