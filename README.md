# Shortener Urlservice

## About

This is a simple *Shortener Urlservice* that stores `UrlData` for an `UrlShortener`.

This project uses:

* Java
* Spring Boot
* MongoDB

**GitHub Actions** are used to build the project, run integration tests, and create Docker images for **Linux** and **MacOS**, pushed to **Docker Hub**: [soserdev/shortener-urlservice](https://hub.docker.com/repository/docker/soserdev/shortener-urlservice/general).

Integration tests for the `UrlServiceController` use [Testcontainers](https://testcontainers.com).

This project is used by:

* [shortener-backend](https://github.com/soserdev/shortener-backend)

---

## API Overview

| Action           | Endpoint                                     | Notes                                                                    |
| ---------------- | -------------------------------------------- | ------------------------------------------------------------------------ |
| Create URL       | `POST /api/v1/urls`                          | `domain`, `shortUrl`, `longUrl`, `user` required                         |
| Get by ID        | `GET /api/v1/urls/{id}`                      | Returns 404 if not found                                                 |
| Update URL       | `PUT /api/v1/urls/{id}`                      | Updates `domain`, `shortUrl`, `longUrl`, `status`; `user` is not updated |
| Get by short URL | `GET /api/v1/urls/short/{domain}/{shortUrl}` | Returns 404 if not found                                                 |
| Get all URLs     | `GET /api/v1/urls`                           | Optional query params: `user` or `domain`                                |

---

## Development

The Urlservice uses MongoDB to store `UrlData`. Docker Desktop is assumed to be running.

* *IntelliJ* can use `compose.yml` to start MongoDB automatically.
* `compose-express.yaml` starts MongoDB + Mongo Express.
* `compose-urlservice.yaml` starts the Urlservice + MongoDB.

---

## Usage with Docker

Build the Docker image:

```bash
docker build -t soserdev/shortener-urlservice:0.1.1-SNAPSHOT -f Dockerfile .
```

Start MongoDB:

```bash
docker compose -f compose.yml up -d
```

Shut it down:

```bash
docker compose -f compose.yml down
```

---

## Run Integration Tests

Run integration tests using Maven (`failsafe-plugin`):

```bash
mvn verify
```

---

## UrlService API Examples

### Create a short URL

```bash
curl -s -H 'Content-Type: application/json' \
     -d '{"domain": "example.com", "shortUrl": "1fa", "longUrl": "http://www.example.com", "user": "default"}' \
     http://localhost:8080/api/v1/urls
```

Response (success):

```json
{
  "id": "69d62a21cef4d076ba7a9dbd",
  "domain": "example.com",
  "shortUrl": "1fa",
  "longUrl": "http://www.example.com",
  "status": "active",
  "user": "default",
  "created": "2026-04-08T12:12:49.818",
  "updated": "2026-04-08T12:12:49.818"
}
```

Response (duplicate `shortUrl` for the same domain):

```json
{
  "timestamp": "2026-04-08T12:20:00.123",
  "status": 422,
  "error": "Url not created (possibly duplicate)!"
}
```

---

### Get by ID

```bash
curl -s http://localhost:8080/api/v1/urls/69d62a21cef4d076ba7a9dbd | jq
```

Success:

```json
{
  "id": "69d62a21cef4d076ba7a9dbd",
  "domain": "example.com",
  "shortUrl": "1fa",
  "longUrl": "http://www.example.com",
  "status": "active",
  "user": "default",
  "created": "2026-04-08T12:12:49.818",
  "updated": "2026-04-08T12:12:49.818"
}
```

Resource not found:

```json
{
  "timestamp": "2026-04-08T12:25:30.456",
  "status": 404,
  "error": "Resource with id: 'nonexistent-id' not found!"
}
```

---

### Get by short URL (domain required)

```bash
curl -s http://localhost:8080/api/v1/urls/short/example.com/1fa | jq
```

Success:

```json
{
  "id": "69d62a21cef4d076ba7a9dbd",
  "domain": "example.com",
  "shortUrl": "1fa",
  "longUrl": "http://www.example.com",
  "status": "active",
  "user": "default",
  "created": "2026-04-08T12:12:49.818",
  "updated": "2026-04-08T12:12:49.818"
}
```

Resource not found:

```json
{
  "timestamp": "2026-04-08T12:26:15.789",
  "status": 404,
  "error": "Resource for domain: 'example.com' and shortUrl: 'missing' not found!"
}
```

---

### Update URL

```bash
curl -s -H 'Content-Type: application/json' -X PUT \
     -d '{"domain": "example.com","shortUrl": "new-short-url", "longUrl": "http://new-long-url", "status": "inactive"}' \
     http://localhost:8080/api/v1/urls/69d62a21cef4d076ba7a9dbd | jq
```

Success:

```json
{
  "id": "69d62a21cef4d076ba7a9dbd",
  "domain": "example.com",
  "shortUrl": "new-short-url",
  "longUrl": "http://new-long-url",
  "status": "inactive",
  "user": "default",
  "created": "2026-04-08T12:12:49.818",
  "updated": "2026-04-08T12:17:49.124"
}
```

Resource not found or conflict:

```json
{
  "timestamp": "2026-04-08T12:27:45.321",
  "status": 404,
  "error": "Resource with id: 'nonexistent-id' not found or conflict occurred!"
}
```

---

### Get all URLs

```bash
curl -s http://localhost:8080/api/v1/urls | jq
```

Optional query parameters:

* `user`: Filter by user ID
* `domain`: Filter by domain

Example:

```bash
curl -s http://localhost:8080/api/v1/urls?user=default | jq
curl -s http://localhost:8080/api/v1/urls?domain=example.com | jq
```

---

### Verify in MongoDB

1. Get the Docker container ID:

```bash
docker ps
```

2. Enter the container:

```bash
docker exec -it <container-id> sh
```

3. Start `mongosh`:

```bash
mongosh -u root -p rootpw
```

4. Use the `urlservice` database:

```bash
use urlservice
show collections
db.urls.find()
db.urls.find({shortUrl:'1fa'})
```

---

### Kubernetes Usage

1. Create persistent volume:

```bash
kubectl apply -f k8s/storage.yaml
```

2. Start MongoDB:

```bash
kubectl apply -f k8s/mongo.yaml
```

3. Start Urlservice:

```bash
kubectl apply -f k8s/api.yaml
```

### Port Forwarding

```bash
kubectl port-forward service/shortener-urlservice 30000:80
```

Access the service via `http://localhost:30000`.

