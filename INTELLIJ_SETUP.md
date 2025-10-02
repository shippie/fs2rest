# IntelliJ IDEA Setup Guide

## Importing the Project

1. Open IntelliJ IDEA
2. Select `File > Open` or `Open` from the welcome screen
3. Navigate to the project directory and select it
4. IntelliJ will automatically detect the Gradle project
5. Wait for Gradle to sync and download dependencies

## Project Structure

The project follows standard Spring Boot conventions with ARC42 architecture:

```
fs2rest/
├── src/
│   ├── main/
│   │   ├── java/de/shippie/fs2rest/
│   │   │   ├── Fs2restApplication.java       # Main application class
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java       # Security configuration
│   │   │   └── service/
│   │   │       ├── FeedGenerator.java        # Feed generation service
│   │   │       └── FileService.java          # File system access service
│   │   └── resources/
│   │       └── application.properties         # Application config
│   └── test/
│       └── java/de/shippie/fs2rest/
│           ├── Fs2restApplicationTests.java
│           └── service/
│               └── FileServiceTests.java
├── build.gradle.kts                          # Gradle build file
├── settings.gradle.kts                       # Gradle settings
└── README.md
```

## Architecture Overview

Based on ARC42 specification:
- **FileService**: Accesses network drive and reads topics (folders)
- **FeedGenerator**: Creates RSS/Atom feeds from file system topics
- **SecurityConfig**: Optional authentication via Keycloak/OpenID Connect
- **TopicController** (to be implemented): REST API with HATEOAS for topics

## Running the Application

### From IntelliJ IDEA:

1. Open `Fs2restApplication.java`
2. Click the green play button next to the `main` method
3. Or use the run configuration dropdown and select/create "Fs2restApplication"

### Run Configuration:

- **Main class**: `de.shippie.fs2rest.Fs2restApplication`
- **VM options** (optional): `-Dspring.profiles.active=dev`
- **Program arguments** (optional): `--server.port=9090`

## Running Tests

### From IntelliJ IDEA:

1. Right-click on `src/test/java` folder
2. Select `Run 'All Tests'`
3. Or run individual test classes/methods

### From Gradle:

1. Open Gradle tool window (View > Tool Windows > Gradle)
2. Navigate to `Tasks > verification > test`
3. Double-click to run

## Debugging

1. Set breakpoints in your code
2. Right-click on `Fs2restApplication.java`
3. Select `Debug 'Fs2restApplication.main()'`
4. Use the debugger controls to step through code

## Java Version

The project is configured to use Java 17 via Gradle toolchain. IntelliJ will automatically download and configure the correct JDK if needed.

To change the Java version:
1. Open `build.gradle.kts`
2. Modify the `languageVersion` setting:
   ```kotlin
   java {
       toolchain {
           languageVersion = JavaLanguageVersion.of(21) // or 25
       }
   }
   ```

## Hot Reload (Spring Boot DevTools)

To enable hot reload during development, add to `build.gradle.kts`:

```kotlin
dependencies {
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}
```

Then restart the application. Changes to code will trigger automatic restart.

## Useful IntelliJ Features

### REST Client
Use IntelliJ's built-in HTTP client to test endpoints:
1. Create a file named `api-test.http`
2. Add requests (to be implemented):
```http
### Get all topics
GET http://localhost:8080/topics

### Get specific topic
GET http://localhost:8080/topics/topic1

### Get Atom feed
GET http://localhost:8080/feed/atom

### Get RSS feed
GET http://localhost:8080/feed/rss
```

### Spring Boot Dashboard
View > Tool Windows > Run Dashboard
Shows all running Spring Boot applications

### Gradle
View > Tool Windows > Gradle
Manage Gradle tasks and dependencies

## Troubleshooting

### Gradle Sync Issues
- File > Invalidate Caches and Restart
- Delete `.gradle` folder and reimport

### JDK Issues
- File > Project Structure > Project > SDK
- Add/select appropriate JDK

### Port Already in Use
Change port in `application.properties`:
```properties
server.port=9090
```

## Code Style

The project uses standard Java code conventions. IntelliJ's default formatter works well.

To format code:
- Windows/Linux: `Ctrl + Alt + L`
- macOS: `Cmd + Option + L`

## Documentation

- [README.md](README.md) - Project overview and quick start
- [API.md](API.md) - Complete API documentation
- [CONFIGURATION.md](CONFIGURATION.md) - Configuration examples
