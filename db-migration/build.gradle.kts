plugins {
    kotlin("jvm") version "2.3.0"
    id("org.liquibase.gradle") version "3.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Database Migration"

kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

repositories {
    mavenCentral()
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.liquibase:liquibase-core:5.0.1")
    }
}

dependencies {
    liquibaseRuntime("org.liquibase:liquibase-core:5.0.1")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:4.0.1")
    liquibaseRuntime("info.picocli:picocli:4.7.7")
    liquibaseRuntime("org.postgresql:postgresql:42.7.9")
}

liquibase {
    activities.register("main") {
        arguments = mapOf(
            "changelogFile" to "src/main/resources/db/changelog/db.changelog-master.yaml",
            "driver" to "org.postgresql.Driver",
            "url" to "jdbc:postgresql://localhost:5432/micro_kotlin_db",
            "username" to "user",
            "password" to "password"
        )
    }
}
