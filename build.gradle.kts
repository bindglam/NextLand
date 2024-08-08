plugins {
    id("java-library")
    kotlin("jvm") version "1.9.22"
    id("com.gradleup.shadow") version "8.3.0"
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
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    implementation("dev.jorel:commandapi-bukkit-shade:9.5.0")

    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(module = "bukkit")
    }
}

tasks.shadowJar {
    archiveFileName = "NextLand.jar"

    dependencies {
        include(dependency("dev.jorel:commandapi-bukkit-shade:9.5.0"))
    }

    relocate("dev.jorel.commandapi", "io.github.bindglam.commandapi")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}