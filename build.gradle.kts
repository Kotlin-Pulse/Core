import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
    `java-library`
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java-library")

    group = "me.pulse"
    version = "0.0.0"

    repositories {
        mavenCentral()
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(21)
        }

        javadoc {
            options.encoding = Charsets.UTF_8.name()
        }

        processResources {
            filteringCharset = Charsets.UTF_8.name()
        }

        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "21"
            }
        }
    }

    kotlin {
        jvmToolchain(21)
    }
}