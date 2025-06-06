plugins {
    id("java-library")
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

group = "io.github.bindglam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    compileOnly("org.purpurmc.purpur:purpur-api:1.21.5-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-core:10.0.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(module = "bukkit")
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    archiveFileName = "NextLand.jar"
}

kotlin {
    jvmToolchain(22)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(22))
}