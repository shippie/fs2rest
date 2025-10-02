# API Documentation

## REST Endpoints

Based on ARC42 architecture, this API provides access to topics (folders) from a network drive.

### Topics Resource (To Be Implemented)

#### List All Topics (with HATEOAS)
```http
GET /topics
Accept: application/hal+json
```

Expected Response:
```json
{
  "_embedded": {
    "topicList": [
      {
        "name": "topic1",
        "path": "/topic1",
        "_links": {
          "self": {
            "href": "http://localhost:8080/topics/topic1"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/topics"
    },
    "atom-feed": {
      "href": "/feed/atom"
    },
    "rss-feed": {
      "href": "/feed/rss"
    }
  }
}
```

#### Get Topic Files
```http
GET /topics/{name}
Accept: application/hal+json
```

Response will vary based on folder structure:
- **Flat listing**: If topic contains "ausgabe" subfolder (configurable keyword)
- **Tree listing**: For all other folder structures

### Feed Resources (To Be Implemented)

#### Atom Feed
```http
GET /feed/atom
Accept: application/atom+xml
```

Returns Atom 1.0 feed with latest file changes from all topics.

#### RSS Feed
```http
GET /feed/rss
Accept: application/rss+xml
```

Returns RSS 2.0 feed with latest file changes from all topics.

## Examples with curl (To Be Implemented)

### Get all topics
```bash
curl http://localhost:8080/topics
```

### Get specific topic files
```bash
curl http://localhost:8080/topics/topic1
```

### Get Atom feed
```bash
curl http://localhost:8080/feed/atom
```

### Get RSS feed
```bash
curl http://localhost:8080/feed/rss
```

## With Basic Authentication

When Basic Authentication is enabled (`security.basic.enabled=true`):

```bash
# Get topics with authentication
curl -u user:password http://localhost:8080/topics
```

## HATEOAS Links

All topic resources will include HATEOAS links for navigation:
- `self`: Link to the current resource
- `all-topics`: Link to all topics
- `atom-feed`: Link to Atom feed
- `rss-feed`: Link to RSS feed

These links follow the HAL (Hypertext Application Language) specification.
