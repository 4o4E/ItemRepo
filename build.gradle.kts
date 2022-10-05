import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    id("com.github.johnrengelman.shadow") version "7.1.2"
    `maven-publish`
    `java-library`
}

group = Versions.group
version = Versions.version

fun kotlinx(id: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$id:$version"

repositories {
    mavenLocal()
    // spigot
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    // sf
    maven("https://jitpack.io")
    // mmo
    maven("https://mvn.lumine.io/repository/maven/")
    // papi
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

dependencies {
    // spigot
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    // eplugin
    implementation("top.e404:eplugin-core:${Versions.eplugin}")
    implementation("top.e404:eplugin-hook-slimefun:${Versions.eplugin}")
    implementation("top.e404:eplugin-hook-mmoitems:${Versions.eplugin}")
    // sf
    compileOnly("com.github.Slimefun:Slimefun4:RC-30")
    // mi
    compileOnly("net.Indyuce:MMOItems:6.7.3")
    // mythic lib
    compileOnly("io.lumine:MythicLib-dist:1.3.1")
    // papi
    compileOnly("me.clip:placeholderapi:2.11.1")
}

tasks {
    withType<KotlinCompile>() {
        kotlinOptions.jvmTarget = "1.8"
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        exclude("META-INF/*")
        relocate("kotlin", "top.e404.itemrepo.kotlin ")
        relocate("top.e404.eplugin", "top.e404.itemrepo.eplugin")

        doFirst {
            for (file in File("jar").listFiles() ?: arrayOf()) {
                println("正在删除`${file.name}`")
                file.delete()
            }
        }

        doLast {
            File("jar").mkdirs()
            for (file in File("build/libs").listFiles() ?: arrayOf()) {
                println("正在复制`${file.name}`")
                file.copyTo(File("jar/${file.name}"), true)
            }
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

afterEvaluate {
    publishing.publications.create<MavenPublication>("java") {
        from(components["kotlin"])
        artifact(tasks.getByName("sourcesJar"))
        artifact(tasks.getByName("javadocJar"))
        artifactId = "item-repo"
        groupId = group as String
        version = version
    }
}