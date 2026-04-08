# Shortener Urlservice

## About

This is a simple _Shortener Urlservice_ that stores `UrlData` for an `UrlShortener`.

This project uses:

- Java
- Spring Boot
- MongoDB

**Github Actions** are used to build the project and run the integration tests. Additionally they create a **Docker Image** both for **Linux** and **MacOS**, and push the image to **Docker Hub** - see [soserdev/shortener-urlservice](https://hub.docker.com/repository/docker/soserdev/shortener-urlservice/general).

Furthermore there are **Integration tests** for the `UrlServiceController` that use [Testcontainers](https://testcontainers.com).

This project is used by:

- [shortener-backend](https://github.com/soserdev/shortener-backend)

## NEW API

Final clean new API:

| Action          | Endpoint                         |
| --------------- | -------------------------------- |
| Create URL      | `POST /api/v1/urls`              |
| Get by ID       | `GET /api/v1/urls/{id}`          |
| Update          | `PUT /api/v1/urls/{id}`          |
| Get by shortUrl | `GET /api/v1/urls/short/{short}` |
| Get by user     | `GET /api/v1/urls?user=abc`      |

## Development

The Urlservice uses MongoDB to store the `UrlData`. We assume Docker Desktop is running in the background.

- _Intellij_ uses the `compose.yml` to start MongoDB if you start the service 
- `compose-express.yaml` starts mongo and mongo express
- `compose-urlservice.yaml` starts the Urlservice and mongo

## Usage Docker

Build the docker image:

```bash
docker build  -t soserdev/shortener-urlservice:0.1.1-SNAPSHOT -f Dockerfile .
```

Start MongoDB using Docker:

```bash
docker compose -f compose.yml up -d
```

Shut it down:

```bash
docker compose -f compose.yml down
```

## Run Integration tests

Since the _failsafe-plugin_ is configured in the `pom.xml` file you can simply run integration tests using maven.

```bash
mvn verify
```


## UrlService API

Create a short url:

```bash
curl -s -H'Content-Type: application/json' -d'{"shortUrl": "1fa","longUrl": "http://www.example.com", "user": "default"}' http://localhost:8080/api/v1/urls
```

My result:

```bash
{"created":"2026-04-08T12:12:49.818459","id":"69d62a21cef4d076ba7a9dbd","longUrl":"http://www.example.com","shortUrl":"1fa","status":"active","updated":"2026-04-08T12:12:49.818473","user":"default"}
```

Get the url by shorturl:

```bash
curl -s -v http://localhost:8080/api/v1/urls/short/1fa | jq
{
  "created": "2026-04-08T12:12:49.818",
  "id": "69d62a21cef4d076ba7a9dbd",
  "longUrl": "http://www.example.com",
  "shortUrl": "1fa",
  "status": "active",
  "updated": "2026-04-08T12:12:49.818",
  "user": "default"
}
```

Get url by id:

```bash
curl -s http://localhost:8080/api/v1/urls/69d62a21cef4d076ba7a9dbd | jq
{
  "created": "2026-04-08T12:12:49.818",
  "id": "69d62a21cef4d076ba7a9dbd",
  "longUrl": "http://www.example.com",
  "shortUrl": "1fa",
  "status": "active",
  "updated": "2026-04-08T12:12:49.818",
  "user": "default"
}
```

Update the url - _'user' is not updated!_:

```bash
curl -s -H'Content-Type: application/json' -X PUT -d'{"shortUrl": "new-short-url","longUrl": "http://new-long-url", "user": "007", "status": "inactive"}' http://localhost:8080/api/v1/urls/69d62a21cef4d076ba7a9dbd | jq
{
  "created": "2026-04-08T12:12:49.818",
  "id": "69d62a21cef4d076ba7a9dbd",
  "longUrl": "http://new-long-url",
  "shortUrl": "new-short-url",
  "status": "inactive",
  "updated": "2026-04-08T12:17:49.124226",
  "user": "default"
}
```

Find all urls for a user `default`:

```bash
curl -s http://localhost:8080/api/v1/urls\?user\=default | jq
```

## Verify the result in MongDB


Use `docker ps` to get the id - here we get `49e2c560b30b` - and login into the docker container.

```bash
> docker exec -it 49e2c560b30b sh
```

Now login into mongo using `mongosh`.

```bash
# mongosh -u root -p rootpw
Current Mongosh Log ID:	666c1b91fc5edebf878db5fa
Connecting to:		mongodb://<credentials>@127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.2.6
Using MongoDB:		7.0.11
Using Mongosh:		2.2.6

For mongosh info see: https://docs.mongodb.com/mongodb-shell/

------
The server generated these startup warnings when booting
2024-06-14T10:27:42.826+00:00: Using the XFS filesystem is strongly recommended with the WiredTiger storage engine. See http://dochub.mongodb.org/core/prodnotes-filesystem
2024-06-14T10:27:43.641+00:00: /sys/kernel/mm/transparent_hugepage/enabled is 'always'. We suggest setting it to 'never' in this binary version
2024-06-14T10:27:43.641+00:00: vm.max_map_count is too low
------
```

Show the databases.

```bash
test> show dbs
admin       100.00 KiB
config       12.00 KiB
local        72.00 KiB
urlservice    8.00 KiB
```

Use the `urlservice` database.

```bash
test> use urlservice
switched to db urlservice
urlservice> show collections
urls
```

Find our documents.

```bash
urlservice> db.urls.find()
[
{
_id: ObjectId('666c1be6a17e5f571be9bbad'),
shortUrl: '1fa',
longUrl: 'http://www.example.com',
user: 'default',
created: ISODate('2024-06-14T10:31:02.079Z'),
updated: ISODate('2024-06-14T10:31:02.079Z'),
_class: 'model.dev.smo.shortener.urlservice.UrlData'
}
]
```

Find a specific document.

```bash
urlservice> db.urls.find({shortUrl:'1fa'})
[
  {
    _id: ObjectId('666c1be6a17e5f571be9bbad'),
    shortUrl: '7765',
    longUrl: 'http://www.example.com',
    user: 'default',
    created: ISODate('2024-06-14T10:31:02.079Z'),
    updated: ISODate('2024-06-14T10:31:02.079Z'),
    _class: 'model.dev.smo.shortener.urlservice.UrlData'
  }
]
```


## Kubernetes Usage

First things first, create the _persistent volume_ and the corresponding _persistent volume claim_.

```bash
kubectl apply -f k8s/storage.yaml
```

Start MongoDB:

```bash
kubectl apply -f k8s/mongo.yaml
```

Start the Urlservice:

```bash
kubectl apply -f k8s/api.yaml
```

### Port Forwarding

In order to access the _url-service_ we have to use _port-forwarding_.

```bash
% kubectl port-forward service/shortener-urlservice 30000:80
Forwarding from 127.0.0.1:30000 -> 80
Forwarding from [::1]:30000 -> 80
Handling connection for 30000
```
Now we can use port `30000` to access the service.