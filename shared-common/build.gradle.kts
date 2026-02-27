plugins {
    kotlin("jvm") version "2.3.0"
    id("java-library")
    id("maven-publish")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Shared Common Library"

kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "nexus"
            url = uri("http://localhost:8081/repository/maven-snapshots/")
            isAllowInsecureProtocol = true
            credentials {
                username = "admin"
                password = "password" // Default password set in docker-compose
            }
        }
    }
}