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

## Development

The Urlservice uses MongoDB to store the ``UrlData`. We assume Docker Desktop is running in the background.

- _Intellij_ uses the `compose.yml` to start MongoDB if you start the service 
- `compose-express.yaml` starts mongo and mongo express
- `compose-urlservice.yaml` starts the Urlservice and mongo

## Usage Docker

Build the docker image:

```bash
docker build  -t soserdev/shortener-urlservice:latest -t soserdev/shortener-urlservice:0.0.1 -f Dockerfile .
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
curl -s -H'Content-Type: application/json' -d'{"shortUrl": "7766","longUrl": "http://www.google.com", "userid": "007"}' http://localhost:30000/api/v1/urlservice
```

My result:

```bash
{"id":"68d6b245dc237d658611c09e","shortUrl":"7765","longUrl":"http://www.google.com","userid":"007","created":"2025-09-26T15:33:25.772331379","updated":"2025-09-26T15:33:25.772686546"
```

Get the short url:

```bash
curl -s http://localhost:30000/api/v1/urlservice/7765 | jq
{
  "id": "68d6b245dc237d658611c09e",
  "shortUrl": "7765",
  "longUrl": "http://www.google.com",
  "userid": "007",
  "created": "2025-09-26T15:33:25.772",
  "updated": "2025-09-26T15:33:25.772",
}
```

Update the url:

```bash
curl -s -H'Content-Type: application/json' -X PUT -d'{"shortUrl": "new-short-url","longUrl": "http://new-long-url", "userid": "007"}' http://localhost:30000/api/v1/urlservice/68d6b245dc237d658611c09e | jq

{
  "id": "68d6b245dc237d658611c09e",
  "shortUrl": "new-short-url",
  "longUrl": "http://new-long-url",
  "userid": "007",
  "created": "2025-09-26T15:33:25.772",
  "updated": "2025-09-26T15:39:16.125269583"
}
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
shortUrl: '7765',
longUrl: 'http://www.google.com',
userid: '007',
created: ISODate('2024-06-14T10:31:02.079Z'),
updated: ISODate('2024-06-14T10:31:02.079Z'),
_class: 'model.dev.smo.shortener.urlservice.UrlData'
}
]
```

Find a specific document.

```bash
urlservice> db.urls.find({shortUrl:'7765'})
[
  {
    _id: ObjectId('666c1be6a17e5f571be9bbad'),
    shortUrl: '7765',
    longUrl: 'http://www.google.com',
    userid: '007',
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