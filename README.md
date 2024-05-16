# Jumper Urlservice

Start mongo using docker compose

```bash
docker compose up -d
```

Create a short url.

```bash
curl -v -H'Content-Type: application/json' -d'{"shortUrl": "7765","longUrl": "http://www.google.com", "userid": "007"}' http://localhost:8082/api/v1/urlservice
```

