# Application Configuration Examples

## File System Topics Configuration

Configure the path to the network drive where topics (folders) are located:

```properties
# Path to network drive with topics
fs2rest.topics.path=/path/to/network/drive

# Keyword for flat file listing (default: "ausgabe")
# When a folder contains this keyword, files are listed flat
# Otherwise, tree structure is preserved
fs2rest.topics.flat-keyword=ausgabe
```

## Basic Authentication

To enable Basic Authentication, create or modify `application.properties`:

```properties
# Enable Basic Authentication
security.basic.enabled=true
```

Default users (for testing):
- Username: `user` | Password: `password` | Role: USER
- Username: `admin` | Password: `admin` | Role: ADMIN, USER

For production, configure Keycloak/OpenID Connect integration (see SecurityConfig.java).

## Custom Port

To change the default port:

```properties
server.port=9090
```

## Production Profile

Create `application-prod.properties` for production settings:

```properties
spring.application.name=fs2rest
server.port=8080

# Production file system paths
fs2rest.topics.path=/mnt/network-drive/topics
fs2rest.topics.flat-keyword=ausgabe

# Security
security.basic.enabled=true

# Logging
logging.level.de.shippie.fs2rest=INFO
logging.level.root=WARN
```

Run with: `./gradlew bootRun --args='--spring.profiles.active=prod'`
