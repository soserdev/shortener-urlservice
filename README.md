# Jumper Urlservice

The _Jumper Urlservice_ stores `UrlData` for an `UrlShortener`.

This project shows

- how to create a docker image
- how to deploy the service to k8s
- how to do some _continuous integration_ using _github actions_
- how to create _integration tests_ using [testcontainers](https://testcontainers.com) to test the `UrlRepository` and `UrlServiceController`.
- how to run unit and integration tests

Prerequisites

- you need to have _Docker_ or _Docker Desktop_ running in order to use testcontainers


## Docker 

### Build Docker Image using `docker build`

Build docker image on MacOS using docker

```bash
docker build  -t jumper/jumper-urlservice:latest -t jumper/jumper-urlservice:0.1 -f Dockerfile .
```

### Build Docker Image using maven

Build docker image using `docker-maven-plugin`

```bash
./mvnw docker:build
```

### Push Docker Image to docker.io

In order to push the image add a `~/.m2/settings.xml` file

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          http://maven.apache.org/xsd/settings-1.0.0.xsd">
      <localRepository/>
      <interactiveMode/>
      <usePluginRegistry/>
      <offline/>
      <pluginGroups>
      <pluginGroup>io.fabric8</pluginGroup>
      <pluginGroup>org.springframework.boot</pluginGroup>
      </pluginGroups>
      <servers>
        <server>
         <id>docker-hub</id>
         <registry>docker.io</registry>
         <username>USERNAME</username>
         <password>PASSWORD</password>
         <configuration>
            <email>EMAIL</email>
          </configuration>
        </server>
      </servers>
      <mirrors/>
      <proxies/>
      <profiles/>
      <activeProfiles/>
</settings>
```

Now you can push it

```bash
./mvnw clean docker:build docker:push
```

### Create a Docker Image using _Paketo Buildpack_

Actually, the best way to create a _Docker Image_ is to use _Paketo Buildpacks_, since you don't need to create a _Dockerfile_.
Paketo will create one for you.

In order to create a Docker Image, you can use maven.

We need some additional configuration in our `pom.xml`.

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <layers>
            <enabled>true</enabled>
            <includeLayerTools>true</includeLayerTools>
        </layers>
        <image>
            <name>jumper/jumper-urlservice:alpaquita-pack</name>
            <builder>bellsoft/buildpacks.builder:musl</builder>
            <env>
                <BP_JVM_VERSION>17</BP_JVM_VERSION>
                <BP_JVM_JLINK_ENABLED>true</BP_JVM_JLINK_ENABLED>
                <BP_JVM_JLINK_ARGS>--no-man-pages --no-header-files --strip-debug --compress=2 --add-modules java.base,java.logging,java.naming,java.desktop,jdk.unsupported</BP_JVM_JLINK_ARGS>
            </env>
        </image>
    </configuration>
</plugin>
```

>_**Note:**_ We have to use Java Version 17 and this will not create an Image for macOs!

```bash
$ docker image ls | grep url
somnidev/jumper-urlservice                0.0.1-SNAPSHOT                                                                7ae7913b4898   About a minute ago   217MB
somnidev/jumper-urlservice                latest                                                                        7ae7913b4898   About a minute ago   217MB
jumper/jumper-urlservice                  alpaquita-pack                                                                b2f191cbc2af   44 years ago         112MB
```
In order to check the architecture the image has, you can use the following commands.

```bash
$ docker image inspect b2f191cbc2af | grep Architecture
        "Architecture": "amd64",
$ docker image inspect 7ae7913b4898 | grep Architecture
        "Architecture": "arm64",
```

### Start a local mongo database

If you want to start the app in _Intellij_ you don't need to start the database via docker compose.
Since this project uses testcontainers just start the app in _Intellij_ and  `compose.yml` will be used to start mongo.

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
_class: 'io.jumper.urlservice.model.UrlData'
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
    _class: 'io.jumper.urlservice.model.UrlData'
  }
]
```


## Kubernetes

### Creating the Kubernetes Configuration for our API

#### Create a Kubernetes Deployment using `kubectl`

Create a _Deployment_ using _kubectl_.

```bash
kubectl create deployment kbe-rest --image springframeworkguru/kbe-rest-brewery --dry-run=client -o=yaml > deployment.yml
```

This creates the following `deployment.yml` file.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  creationTimestamp: null
  labels:
    app: jumper-urlservice
  name: jumper-urlservice
spec:
  replicas: 1
  selector:
    matchLabels:
      app: jumper-urlservice
  strategy: {}
  template:
    metadata:
      creationTimestamp: null
      labels:
        app: jumper-urlservice
    spec:
      containers:
        - image: somnidev/jumper-urlservice
          name: jumper-urlservice
          resources: {}
status: {}
```

### Create a Kubernetes Service using `kubectl`

Now we need a kubernetes service for our deployment.

```bash
kubectl create service clusterip kbe-rest --tcp=8080:8080 --dry-run=client -o=yaml > service.yml
```

The `service.yml` file.

```yaml
apiVersion: v1
kind: Service
metadata:
  creationTimestamp: null
  labels:
    app: jumper-urlservice
  name: jumper-urlservice
spec:
  ports:
  - name: 8080-8080
    port: 8080
    protocol: TCP
    targetPort: 8080
  selector:
    app: jumper-urlservice
  type: ClusterIP
status:
  loadBalancer: {}
```

### The final Kubernetes Deployment and Service

The final `api.yml` file contains the following configuration.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jumper-urlservice
  labels:
    app: jumper-urlservice
spec:
  replicas: 1
  selector:
    matchLabels:
      app: jumper-urlservice
  template:
    metadata:
      labels:
        app: jumper-urlservice
    spec:
      containers:
      - name: jumper-urlservice
        image: somnidev/jumper-urlservice:0.0.1-SNAPSHOT
        env:
          - name: SPRING_PROFILES_ACTIVE
            value: production

---
apiVersion: v1
kind: Service
metadata:
  name: jumper-urlservice
spec:
  ports:
  - name: http
    port: 80
    protocol: TCP
    targetPort: 80

  selector:
    app: jumper-urlservice
  type: ClusterIP
```

### Deploy our service to Kubnernetes

#### Start MongoDB

Now we change to the `k8s` directory.

```bash
cd k8s
```

In order to start MongoDB we first have to create our _persistent volume_ and the corresponding _persistent volume claim_.

```bash
kubectl apply -f storage.yaml
```

It might take some time to create your `pvc`.

```bash
% kubectl get pvc
NAME        STATUS    VOLUME   CAPACITY   ACCESS MODES   STORAGECLASS     VOLUMEATTRIBUTESCLASS   AGE
mongo-pvc   Pending                                      mylocalstorage   <unset>                 12s
```

But after some time, there should be one _pvc_ configured and one corresponding _pv_.

```bash
% kubectl get pvc
NAME        STATUS   VOLUME          CAPACITY   ACCESS MODES   STORAGECLASS     AGE
mongo-pvc   Bound    local-storage   10Gi       RWO            mylocalstorage   2m32s

% kubectl get pv 
NAME            CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM               STORAGECLASS     REASON   AGE
local-storage   10Gi       RWO            Retain           Bound    default/mongo-pvc   mylocalstorage            3m12s
```

Now we can start MongoDB itself.

```bash
kubectl apply -f mongo.yaml
```

And check if it is running.

```bash
% kubectl get all
NAME                          READY   STATUS    RESTARTS   AGE
pod/mongodb-86fb498bb-zcpsg   1/1     Running   0          12m

NAME                     TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)     AGE
service/jumper-mongodb   ClusterIP   10.108.219.137   <none>        27017/TCP   12m
service/kubernetes       ClusterIP   10.96.0.1        <none>        443/TCP     5d20h

NAME                      READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/mongodb   1/1     1            1           12m

NAME                                DESIRED   CURRENT   READY   AGE
replicaset.apps/mongodb-86fb498bb   1         1         1       12m
```

### Access the database

In order to access the container and the database use kubectl.

```bash
% kubectl exec --stdin --tty mongodb-86fb498bb-zcpsg -- /bin/bash
```

Now we can run `mongosh`.  The `mongo` command is not supported since version 6.

```bash
% mongosh
Current Mongosh Log ID:	664dfcc84786086433554f36
Connecting to:		mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.1.4
Using MongoDB:		7.0.5
Using Mongosh:		2.1.4

For mongosh info see: https://docs.mongodb.com/mongodb-shell/


To help improve our products, anonymous usage data is collected and sent to MongoDB periodically (https://www.mongodb.com/legal/privacy-policy).
You can opt-out by running the disableTelemetry() command.

------
   The server generated these startup warnings when booting
   2024-05-22T13:57:09.498+00:00: Access control is not enabled for the database. Read and write access to data and configuration is unrestricted
   2024-05-22T13:57:09.498+00:00: /sys/kernel/mm/transparent_hugepage/enabled is 'always'. We suggest setting it to 'never'
   2024-05-22T13:57:09.498+00:00: vm.max_map_count is too low
------

```

Let's show all available databases.

```bash
test> show dbs
admin   40.00 KiB
config  12.00 KiB
local   40.00 KiB
```

In order to get logged out we have to `exit` twice!

### Port Forwarding

In order to access the _url-service_ we have to use _port-forwarding_.

```bash
% kubectl port-forward service/jumper-urlservice 30000:80
Forwarding from 127.0.0.1:30000 -> 80
Forwarding from [::1]:30000 -> 80
Handling connection for 30000
```
Now we can use port `30000` to access the service.

```bash
% curl -v localhost:30000/shorturl/MjQ0MD
*   Trying [::1]:30000...
* Connected to localhost (::1) port 30000
> GET /shorturl/MjQ0MD HTTP/1.1
> Host: localhost:30000
> User-Agent: curl/8.4.0
> Accept: */*
>
< HTTP/1.1 404
< Vary: Origin
< Vary: Access-Control-Request-Method
< Vary: Access-Control-Request-Headers
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Fri, 14 Jun 2024 17:05:12 GMT
<
* Connection #0 to host localhost left intact
{"timestamp":"2024-06-14T17:05:12.504+00:00","status":404,"error":"Not Found","path":"/shorturl/MjQ0MD"}
```

### Readiness & Liveness & Graceful Shutdown

In order to offer Readiness & Liveness we have to configure it in our deployment file.
See [Spring Framework 6: Beginner to Guru - 509. KBE - Add Readiness and Liveness Probe Configuration](https://www.udemy.com/course/spring-framework-6-beginner-to-guru/learn/lecture/36116594)
and the [Source Code](https://github.com/springframeworkguru/kbe-sb-microservices/blob/main/k8s-scripts/beer-service-deployment.yml).


## UserService API

### GET-Request

```bash
curl http://localhost:8082/api/v1/urlservice/7765c | jq
```

### POST-Request

Create a short url.

```bash
curl -v -H'Content-Type: application/json' -d'{"shortUrl": "7765","longUrl": "http://www.google.com", "userid": "007"}' http://localhost:8082/api/v1/urlservice
```

### PUT-Request

In order to update a entity, we first have to create one.

```bash
curl -v -H'Content-Type: application/json' -d'{"shortUrl": "7765c","longUrl": "http://www.google.com", "userid": "007"}' http://localhost:8082/api/v1/urlservice | jq
{
  "id": "6672fcf9bb0b5e097c2f1631",
  "shortUrl": "7765c",
  "longUrl": "http://www.google.com",
  "userid": "007",
  "created": "2024-06-19T17:44:57.719322",
  "updated": "2024-06-19T17:44:57.71934"
```

Now we can update the entity.

```bash
curl -v -H'Content-Type: application/json' -X PUT -d'{"shortUrl": "new-short-url","longUrl": "http://new-long-url", "userid": "007"}' http://localhost:8082/api/v1/urlservice/6672fcf9bb0b5e097c2f1631 | jq
{
  "id": "6672fcf9bb0b5e097c2f1631",
  "shortUrl": "new-short-url",
  "longUrl": "http://new-long-url",
  "userid": "007",
  "created": "2024-06-19T17:44:57.719",
  "updated": "2024-06-19T17:48:34.616052"
}
```

## Quality Assurance

### Running unit tests

To run all unit tests just type the following command.

```bash
mvn test
```

### Running Integration tests

Since the _failsafe-plugin_ is configured in the `pom.xml` file you can simply run integration tests using maven.

```bash
mvn verify
```

### Choosing Between MockMvc and @SpringBootTest for Controller Testing

You can use either `@WebMvcTest` and `MockMvc` or `@SpringBootTest` and `TestRestTemplate` for Controller Testing. 
`MockMvc` is a faster and more lightweight slice-test.
On the other side `@SpringBootTest` starts the entire application context and a real servlet container, 
where you can ensure that all filters and converters are properly executed.
See [Choosing Between MockMvc and @SpringBootTest for Controller Testing](https://rieckpil.de/choosing-between-mockmvc-and-springboottest-for-testing/).

### Code-Coverage with Jacoco

JaCoCo (Java Code Coverage) is an open-source tool used to measure and report the extent to which Java code is executed during tests. 
It is widely used in Spring Boot applications to ensure code quality and reliability by identifying untested parts of the codebase.

JaCoCo can be integrated using the Maven plugin. This involves adding the JaCoCo plugin to the pom.xml file and configuring it to generate coverage reports during the build process.

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>prepare-package</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

After executing the command `mvn clean verify` we can take a look at the `target/site/jacoco/index.html` page to see what the generated report looks like.

In a real world project, as developments go further, we need to keep track of the code coverage score.
JaCoCo offers a simple way of declaring minimum requirements that should be met, otherwise the build will fail.

```xml
<execution>
    <id>jacoco-check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.30</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

It is recommended to exclude certain packages or directories, e.g. `model` package, since we don't test them.

```xml
<configuration>
    <excludes>
        <exclude>**/config/*</exclude>
        <exclude>**/model/*</exclude>
        <exclude>**/repository/*</exclude>
        <exclude>**/exception/*</exclude>
    </excludes>
</configuration>
```

You'll find a good introduction to _Jacoco_ at [Baeldung - Intro to JaCoCo](https://www.baeldung.com/jacoco)

Examples can be found here:
- [React & Spring Boot Hateoas Driven Fullstack Application on Kubernetes](https://suaybsimsek58.medium.com/react-spring-boot-hateoas-driven-fullstack-application-on-kubernetes-7ea33894d12b)
- [Jacoco maven plugin](https://github.com/susimsek/HateoasFullstackApp/blob/main/hateoas-backend/pom.xml)
- [Hateoas Fullstack App Using Spring Boot & React](https://github.com/susimsek/HateoasFullstackApp)


## References

- [Dan Vega - Test Driven Development (TDD) in Spring](https://www.youtube.com/watch?v=-H5sud1-K5A&t=2297s)
- [Dan Vega - Spring Boot Testcontainers - Integration Testing made easy!](https://www.youtube.com/watch?v=erp-7MCK5BU&t=444s)
- [datmt - Testcontainers with MongoDB & Spring Boot](https://www.youtube.com/watch?v=9_1hkYVQ1eI)
- [Hateoas Fullstack App Using Spring Boot & React](https://github.com/susimsek/HateoasFullstackApp/tree/main)
- [Techworld with Nana - GitHub Actions Tutorial - Basic Concepts and CI/CD Pipeline with Docker](https://www.youtube.com/watch?v=R8_veQiYBjI)

