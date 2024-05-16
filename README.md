# Jumper Urlservice

Build docker image using docker

```bash
docker build  -t jumper/jumper-urlservice:latest -t jumper/jumper-urlservice:0.1 -f Dockerfile .
```

Build docker image using `docker-maven-plugin`

```bash
./mvnw docker:build
```


Start mongo using docker compose

```bash
docker compose up -d
```

Create a short url.

```bash
curl -v -H'Content-Type: application/json' -d'{"shortUrl": "7765","longUrl": "http://www.google.com", "userid": "007"}' http://localhost:8082/api/v1/urlservice
```

