# fs2rest

A modern REST API application built with Spring Boot 3, Java (configurable), and Gradle Kotlin DSL.

Based on ARC42 architecture for providing topics (folders) from a network drive as REST API with HATEOAS and Feed support.

## Features

- **Spring Boot 3** (Latest version 3.4.1)
- **Java 17+** with Gradle toolchain support (configured for Java 17, can be upgraded to Java 25 when available)
- **REST API** with HATEOAS for navigating file system topics
- **Atom/RSS Feed** support for subscribable content changes
- **Optional Basic Authentication** (configurable with Keycloak/OpenID Connect)
- **File System Access** to read topics and files from network drive
- **Gradle Kotlin DSL** for build configuration
- **IntelliJ IDEA** ready

## Requirements

- Java 17 or higher (configured via Gradle toolchain)
- Gradle 8.11.1+ (included via wrapper)

> **Note**: The project is configured for Java 17 currently. To use Java 25 when it becomes available, simply change the `languageVersion` in `build.gradle.kts` to `JavaLanguageVersion.of(25)`.

## Building the Application

```bash
./gradlew build
```

## Running the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## API Endpoints

### Topics API (REST with HATEOAS)

**To be implemented:**
- `GET /topics` - Get all topics (folders) with HATEOAS links
- `GET /topics/{name}` - Get files for a specific topic
  - Flat representation for folders with "ausgabe" keyword
  - Tree representation for other folder structures

### Feed Endpoints

**To be implemented:**
- `GET /feed/atom` - Get Atom feed (application/atom+xml)
- `GET /feed/rss` - Get RSS feed (application/rss+xml)

## Example API Usage

**To be implemented:** API endpoints will provide access to file system topics.

## Configuration

Configuration can be modified in `src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# Network drive path for topics
fs2rest.topics.path=/path/to/network/drive

# Keyword for flat file listing (default: "ausgabe")
fs2rest.topics.flat-keyword=ausgabe

# Enable/disable Basic Authentication
security.basic.enabled=false
```

### Enabling Basic Authentication

To enable optional Basic Authentication:

1. Set `security.basic.enabled=true` in `application.properties`
2. Configure Keycloak/OpenID Connect (see SecurityConfig.java)
3. Or use test credentials:
   - Username: `user` / Password: `password` (USER role)
   - Username: `admin` / Password: `admin` (ADMIN role)

## Running Tests

```bash
./gradlew test
```

## Development with IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. Import as a Gradle project
3. The IDE will automatically configure Java 25 via Gradle toolchain
4. Run the application using the `Fs2restApplication` main class

## Technology Stack

- **Spring Boot 3.4.1** - Application framework
- **Spring Web** - RESTful web services
- **Spring HATEOAS** - Hypermedia support
- **Spring Security** - Optional authentication
- **Rome Tools** - Atom/RSS feed generation
- **JUnit 5** - Testing framework
- **Gradle 8.11.1** - Build tool with Kotlin DSL

## License

BSD 2-Clause License - see LICENSE file for details