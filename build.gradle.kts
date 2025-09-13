import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    idea
    jacoco
}

group = "com.robotutor"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()

//    fun githubMavenRepository(name: String) {
//        maven {
//            url = uri("https://maven.pkg.github.com/IOT-echo-system/$name")
//            credentials {
//                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
//                password = project.findProperty("gpr.token") as String? ?: System.getenv("TOKEN")
//            }
//        }
//    }
//
//    githubMavenRepository("logging-starter")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.projectreactor.kafka:reactor-kafka")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools:2.5.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring30x:4.11.0")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo.packageresolver:4.11.3")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.apache.commons:commons-lang3:3.18.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }

}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs(
        "--add-opens", "java.base/java.time=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang=ALL-UNNAMED"
    )
}

sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += output + compileClasspath
    }
}


configurations {
    named("integrationTestImplementation") {
        extendsFrom(configurations.testImplementation.get())
    }
    named("integrationTestRuntimeOnly") {
        extendsFrom(configurations.testRuntimeOnly.get())
    }
}

dependencies {
    "integrationTestImplementation"(sourceSets.test.get().output)
}

tasks.register<Test>("integrationTest") {
    description = "Runs integration tests with embedded Mongo and Kafka"
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    useJUnitPlatform()
    shouldRunAfter("test")
}

tasks.check {
    dependsOn("integrationTest")
}

tasks.withType<Test>().configureEach {
    val agentJar = classpath.files.firstOrNull { it.name.startsWith("mockito-agent") && it.extension == "jar" }
    if (agentJar != null) {
        jvmArgs("-javaagent=${agentJar.absolutePath}")
    }
}

// Jacoco configuration

//tasks.test {
//    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
//}
//
//tasks.jacocoTestReport {
//    dependsOn(tasks.test) // tests are required to run before generating the report
//    finalizedBy(tasks.jacocoTestCoverageVerification) // coverage verification is always performed after tests run
//}
//
//tasks.jacocoTestCoverageVerification {
//    dependsOn(tasks.jacocoTestReport) // tests are required to run before generating the report
//}
//
//jacoco {
//    toolVersion = "0.8.7"
//}
//
//tasks.jacocoTestCoverageVerification {
//    violationRules {
//        rule {
//            limit {
//                minimum = BigDecimal("0.41")
//            }
//        }
//
//        rule {
//            limit {
//                counter = "BRANCH"
//                minimum = "0.50".toBigDecimal()
//            }
//        }
//    }
//}