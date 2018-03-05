import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion
val kotlinxCoroutinesVersion = "0.22.2"

plugins {
    kotlin("jvm") version "1.2.30"
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    compile(kotlin("stdlib-jre8", kotlinVersion))
    compile(kotlin("reflect", kotlinVersion))
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

    compile("org.slf4j:slf4j-api:1.7.14")
    compile("ch.qos.logback:logback-classic:1.1.3")

    testCompile(kotlin("test-junit", kotlinVersion))
    testCompile("junit:junit:4.11")
    testCompile("org.eclipse.jetty:jetty-server:9.4.7.v20170914")
}

repositories {
    mavenCentral()
    jcenter()
}

val fatJar = task("fatJar", type = Jar::class) {
    val version = "0.0.1-SNAPSHOT"
    val applicationName = "sekurity"
    baseName = "$applicationName-$version"
    from(configurations.runtime.map {
        if (it.isDirectory) it else zipTree(it)
    })
    with(tasks["jar"] as CopySpec)
}

tasks {
    withType(GradleBuild::class.java) {
        dependsOn(fatJar)
    }

    withType(Test::class.java) {
        testLogging {
            showStandardStreams = true
        }
    }
}
