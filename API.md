# API Documentation

## REST Endpoints

### Articles Resource

#### List All Articles (with HATEOAS)
```http
GET /api/articles
Accept: application/hal+json
```

Response:
```json
{
  "_embedded": {
    "articleList": [
      {
        "id": 1,
        "title": "Article Title",
        "content": "Article content...",
        "publishedDate": "2025-10-02T07:40:59.090894733",
        "author": "Admin",
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/articles/1"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/articles"
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

#### Get Single Article
```http
GET /api/articles/{id}
Accept: application/hal+json
```

#### Create Article
```http
POST /api/articles
Content-Type: application/json

{
  "title": "New Article",
  "content": "Article content",
  "author": "John Doe"
}
```

#### Update Article
```http
PUT /api/articles/{id}
Content-Type: application/json

{
  "title": "Updated Title",
  "content": "Updated content",
  "publishedDate": "2025-10-02T10:00:00",
  "author": "Jane Doe"
}
```

#### Delete Article
```http
DELETE /api/articles/{id}
```

### Feed Resources

#### Atom Feed
```http
GET /feed/atom
Accept: application/atom+xml
```

Returns Atom 1.0 feed with all articles.

#### RSS Feed
```http
GET /feed/rss
Accept: application/rss+xml
```

Returns RSS 2.0 feed with all articles.

## Examples with curl

### Get all articles
```bash
curl http://localhost:8080/api/articles
```

### Get specific article
```bash
curl http://localhost:8080/api/articles/1
```

### Create article
```bash
curl -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My Article",
    "content": "Article content here",
    "author": "John Doe"
  }'
```

### Update article
```bash
curl -X PUT http://localhost:8080/api/articles/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Title",
    "content": "Updated content",
    "publishedDate": "2025-10-02T10:00:00",
    "author": "Jane Doe"
  }'
```

### Delete article
```bash
curl -X DELETE http://localhost:8080/api/articles/1
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
# Get articles with authentication
curl -u user:password http://localhost:8080/api/articles

# Create article with authentication
curl -u admin:admin -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Authenticated Article",
    "content": "Content",
    "author": "Admin"
  }'
```

## HATEOAS Links

All article resources include HATEOAS links for navigation:
- `self`: Link to the current resource
- `all-articles`: Link to all articles
- `atom-feed`: Link to Atom feed
- `rss-feed`: Link to RSS feed

These links follow the HAL (Hypertext Application Language) specification.
