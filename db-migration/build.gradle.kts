plugins {
    java
    id("org.liquibase.gradle") version "3.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Database Migration"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
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
            "changelogFile" to "db/changelog/db.changelog-master.yaml",
            "searchPath" to project.file("src/main/resources").absolutePath,
            "driver" to "org.postgresql.Driver",
            "url" to "jdbc:postgresql://localhost:5432/micro_kotlin",
            "username" to "postgres",
            "password" to "password"
        )
    }
}
