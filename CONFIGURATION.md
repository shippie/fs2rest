# Application Configuration Examples

## Basic Authentication

To enable Basic Authentication, create or modify `application.properties`:

```properties
# Enable Basic Authentication
security.basic.enabled=true
```

Default users:
- Username: `user` | Password: `password` | Role: USER
- Username: `admin` | Password: `admin` | Role: ADMIN, USER

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
security.basic.enabled=true
logging.level.de.shippie.fs2rest=WARN
```

Run with: `./gradlew bootRun --args='--spring.profiles.active=prod'`
