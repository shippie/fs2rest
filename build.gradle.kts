plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.mifa"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Feed support (Rome for Atom/RSS)
    implementation("com.rometools:rome:2.1.0")
    // OpenAPI/Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
    // Gradle lädt den Agent beim Teststart. Pfad wird vom Dependency-Cache aufgelöst.
    // Byte Buddy Agent für Mockito statisch anhängen (verhindert Self-Attach-Warnungen)
//    val agentPath = classpath.asPath
//        .split(File.pathSeparator)
//        .firstOrNull { it.contains("byte-buddy-agent") }
//
//    if (agentPath != null) {
//        jvmArgs("-javaagent=$agentPath")
//    }
//    jvmArgs("-XX:+EnableDynamicAgentLoading")
}
