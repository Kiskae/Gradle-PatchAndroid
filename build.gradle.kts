import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.10.0"
    kotlin("jvm") version "1.3.11"
}

group = "net.serverpeon.gradle"
version = "1.0-beta01"

repositories {
    mavenCentral()
    google()
}

configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion(KotlinCompilerVersion.VERSION)
        }
    }
}

dependencies {
    api(gradleApi())
    api(kotlin("stdlib-jdk8"))
    // TODO: replace with gradle-api when the API is linked in and finalized
    api("com.android.tools.build:gradle:3.2.1")
}

pluginBundle {
    tags = listOf("")
}

gradlePlugin {
    plugins {
        create("patchAndroidPlugin") {
            id = "net.serverpeon.gradle.patchandroid"
            displayName = "PatchAndroid"
            description = "Simplify common configuration of android submodules"
            implementationClass = "net.serverpeon.gradle.patchandroid.PatchAndroidPlugin"
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}