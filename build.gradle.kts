import com.google.devtools.ksp.gradle.KspTaskJvm

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.0"
    id("com.google.devtools.ksp") version "1.9.0-1.0.11"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.0.0-M8"
}

version = "0.1"
group = "com.example"

val kotlinVersion = project.properties.get("kotlinVersion")
val kotlinVersionEnum = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_8
val jvmTargetEnum = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_19
val javaVersion = "19"
val kotlinLangVersion = "1.8"

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        mavenContent { snapshotsOnly() }
    }
    mavenCentral()
}

dependencies {
    ksp("info.picocli:picocli-codegen")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    implementation("info.picocli:picocli")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.picocli:micronaut-picocli")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    compileOnly("org.graalvm.nativeimage:svm")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
}


application {
    mainClass.set("com.example.DemoCommand")
}

java {
    sourceCompatibility = JavaVersion.toVersion("19")
}

tasks {
    compileKotlin {
        compilerOptions {
            apiVersion.set(kotlinVersionEnum)
            languageVersion.set(kotlinVersionEnum)
            jvmTarget.set(jvmTargetEnum)
        }
    }
    compileTestKotlin {
        compilerOptions {
            apiVersion.set(kotlinVersionEnum)
            languageVersion.set(kotlinVersionEnum)
            jvmTarget.set(jvmTargetEnum)
        }
    }
}

graalvmNative {
  binaries {
    named("test") {
      buildArgs.addAll(listOf(
        "--verbose",
        "--enable-preview",
        "--native-image-info",
        "-H:+ReportExceptionStackTraces",
        "--trace-class-initialization=kotlin.annotation.AnnotationTarget,kotlin.annotation.AnnotationRetention",
      ))
    }
  }
}

micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.example.*")
    }
}

afterEvaluate {
  tasks.named<KspTaskJvm>("kspKotlin").configure {
    kotlinOptions {
      apiVersion = kotlinLangVersion
      languageVersion = kotlinLangVersion
      jvmTarget = javaVersion
    }
  }
}
