import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.JavaVersion.VERSION_21
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.run.paper)
}

group = "de.ole101.chained"
version = "1.0-SNAPSHOT"
description = "A Minecraft plugin that tries to recreate the game Chained Together"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
}

dependencies {
    implementation(rootProject.libs.lombok)
    annotationProcessor(rootProject.libs.lombok)
    implementation(rootProject.libs.guice)
    implementation(rootProject.libs.annotations)
    implementation(rootProject.libs.classindex)
    annotationProcessor(rootProject.libs.classindex)

    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = VERSION_21.toString()
    targetCompatibility = VERSION_21.toString()
    options.encoding = "UTF-8"
}

tasks {
    test {
        useJUnitPlatform()
    }

    named<Jar>("jar") {
        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    named<ShadowJar>("shadowJar") {
        archiveFileName.set("${project.name}-${project.version}-all.jar")
    }

    named<ProcessResources>("processResources") {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("paper-plugin.yml") {
                expand("version" to project.version)
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    named<RunServer>("runServer") {
        minecraftVersion("1.21.1")
    }

    build {
        dependsOn(named("shadowJar"))
    }
}
