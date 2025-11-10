import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("jvm") version "2.2.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
    id("maven-publish")
}

group = "io.magesoftware"
version = "0.0.1"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "io.magesoftware"
            artifactId = "ktoon"
            version = "0.0.1"

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ssimiao/ktoon")
            credentials {
                // Ensure environment variables are correctly accessed
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
