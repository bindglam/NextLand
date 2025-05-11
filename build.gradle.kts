plugins {
    id("java-library")
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

group = "io.github.bindglam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("dev.jorel:commandapi-bukkit-shade:10.0.0")

    compileOnly("dev.folia:folia-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(module = "bukkit")
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    archiveFileName = "NextLand.jar"

    relocate("dev.jorel.commandapi", "io.github.bindglam.commandapi")
}

kotlin {
    jvmToolchain(21)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}