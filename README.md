# Shortener Urlservice

## About

The _Shortener Urlservice_ stores `UrlData` for an `UrlShortener`.

This project:

- uses **Github Actions** to build the project, run the integration tests, create a docker image for linux and macos, and push it to **Docker Hub**
- has _integration tests_ using [testcontainers](https://testcontainers.com) to test the `UrlRepository` and `UrlServiceController`.
- can be deployed to k8s

### Start a local mongo database for development

If you want to start the app in _Intellij_ you don't need to start the database via docker compose.
Since this project uses testcontainers just start the app in _Intellij_ and  `compose.yml` will be used to start mongodb.

But there is a also a `docker-compose.yaml` file that allows us to start a mongo and mongo express on `localhost`.
In order to start them for local development using docker compose you can use the following docker command.

```bash
docker compose -f docker-compose.yml up -d
```

If you want to shut them down, you have to use the following command.

```bash
docker compose -f docker-compose.yml down
```

If you don't specify a file, docker compose will use the `compose.yml` file.

## Use docker for development


Let's login into our docker container.

```bash
> docker exec -it 49e2c560b30b sh
```

Let's login into our mongo database using `mongosh`.

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


### Deploy the UrlService to Kubnernetes

In order to start MongoDB we first have to create our _persistent volume_ and the corresponding _persistent volume claim_.

```bash
cd k8s
kubectl apply -f storage.yaml
```

Now we can start MongoDB itself.

```bash
kubectl apply -f mongo.yaml
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


## UrlService API

Create a short url:

```bash
curl -s -H'Content-Type: application/json' -d'{"shortUrl": "7766","longUrl": "http://www.google.com", "userid": "007"}' http://localhost:30000/api/v1/urlservice
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
