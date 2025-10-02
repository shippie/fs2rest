# fs2rest

A modern REST API application built with Spring Boot 3, Java (configurable), and Gradle Kotlin DSL.

## Features

- **Spring Boot 3** (Latest version 3.4.1)
- **Java 17+** with Gradle toolchain support (configured for Java 17, can be upgraded to Java 25 when available)
- **REST API** with full CRUD operations
- **HATEOAS** support for hypermedia-driven API
- **Atom/RSS Feed** support for content syndication
- **Optional Basic Authentication** (configurable)
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

### Articles API (REST with HATEOAS)

- `GET /api/articles` - Get all articles with HATEOAS links
- `GET /api/articles/{id}` - Get a specific article
- `POST /api/articles` - Create a new article
- `PUT /api/articles/{id}` - Update an article
- `DELETE /api/articles/{id}` - Delete an article

### Feed Endpoints

- `GET /feed/atom` - Get Atom feed (application/atom+xml)
- `GET /feed/rss` - Get RSS feed (application/rss+xml)

## Example API Usage

### Get all articles
```bash
curl http://localhost:8080/api/articles
```

### Create a new article
```bash
curl -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My Article",
    "content": "Article content",
    "author": "John Doe"
  }'
```

### Get Atom feed
```bash
curl http://localhost:8080/feed/atom
```

### Get RSS feed
```bash
curl http://localhost:8080/feed/rss
```

## Configuration

Configuration can be modified in `src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# Enable/disable Basic Authentication
security.basic.enabled=false
```

### Enabling Basic Authentication

To enable optional Basic Authentication:

1. Set `security.basic.enabled=true` in `application.properties`
2. Use credentials:
   - Username: `user` / Password: `password` (USER role)
   - Username: `admin` / Password: `admin` (ADMIN role)

Example with authentication:
```bash
curl -u user:password http://localhost:8080/api/articles
```

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