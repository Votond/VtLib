plugins {
    kotlin("jvm") version "2.1.21"
    id("com.gradleup.shadow") version "8.3.6"
    id("de.eldoria.plugin-yml.bukkit") version "0.7.1"
}

kotlin {
    // CommandAPI requires Java 16
    jvmToolchain(16)
}

group = "me.votond.vtlib"
description = "Utility library for Bukkit plugin development"
version = "1.0"

bukkit {
    name = "VtLib"
    author = "Votond"
    main = "me.votond.vtlib.VtLib"
    apiVersion = "1.16"
    foliaSupported = false
}

tasks {
    shadowJar {
        dependsOn(jar)
        minimize()
        archiveFileName = "VtLib-$version.jar"
    }
}

repositories {
    mavenCentral()
    maven {
        name = "PaperMC"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "SpigotMC"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "CommandAPI"
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }
    maven {
        name = "AuthLib"
        url = uri("https://libraries.minecraft.net/")
    }
}

dependencies {
    // Base
    api(kotlin("stdlib"))
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    api("com.charleskorn.kaml:kaml:0.78.0")
    // Convenient commands API
    // Do not change the version
    val commandApiVersion = "9.7.0"
    api("dev.jorel:commandapi-bukkit-shade:$commandApiVersion")
    api("dev.jorel:commandapi-bukkit-kotlin:$commandApiVersion")
    // Inventory API
    api("com.github.stefvanschie.inventoryframework:IF:0.11.0")
    // Rich text support
    val adventureVersion = "4.21.0"
    api("net.kyori:adventure-platform-bukkit:4.4.0")
    api("net.kyori:adventure-api:$adventureVersion")
    api("net.kyori:adventure-text-minimessage:$adventureVersion")
    api("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    // Do not change the version
    compileOnly("com.mojang:authlib:1.5.21")
}