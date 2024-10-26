## Introduction
This project is an example of Kafka connector with Debezium.

The repository contains a Spring Boot project `job-api` that will apply the needed flyway migrations and will serve as an example to feed the listened `job` table.

The flyway migration need to be applied before the connector is created but after the database is running. That's why there are two docker-compose files in the project.

## Run the project
### Start the database
Run the `docker/docker-compose-database.yml` to start the database
```
docker-compose -f docker/docker-compose-database.yml up -d
```

### Movie API
Once the database is started, you can start the Spring Boot project `movie-api` and try it out.

Save a movie
```
curl --request POST \
  --url http://localhost:8080/movie-api/movies \
  --header 'Content-Type: application/json' \
  --data '{
	"id": 26,
	"title": "Some movie title",
	"release_date": "2022-02-26"
}'
```

Delete a movie
```
curl --request DELETE \
  --url http://localhost:8080/movie-api/movies/26
```

### Kafka environment
To deploy the kafka required environment, just run the `docker/docker-compose.yml` file.

It will launch different containers:
- zookeeper
- kafka
- debezium
- akhq: a browser GUI to check out topics, messages and schemas
- init-kafka: init container to create the required Kafka topic and connector


```
docker-compose -f docker/docker-compose.yml up -d
```

You will be able to access akhq on [this url](http://localhost:8190/) to view the events pushed by debezium in the topic
