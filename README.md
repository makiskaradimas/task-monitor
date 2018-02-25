# Redis Task Monitor

A restful micro service which will continuously calculate the average time it takes to
perform a named task.

### Prerequisites
* Docker
* Docker Compose
* Maven

### Build docker image

```
mvn clean package docker:build
```

### Run the microservice

```
docker-compose up
```

### Test the application

#### Task performed
##### POST http://localhost:8080/tasks
Headers:
```
Content-Type: application/json
```
Sample request body:
```
{ "id": "test", "milliseconds": 500 }
```
#### Average Duration
##### GET http://localhost:8080/tasks/:id/duration
Sample response body:
```
{ "id": "test", "averageDuration": 500.0 }
```