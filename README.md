# Saga Demo

Slides are available at <http://bit.ly/sagakafka>

To Execute this project, clone the repository, `cd` into it, and execute:

    docker-compose up

You can use the [Insominia file](Insomnia.json) to POST a `new order` to <http://localhost:8080/orchestration/order>

You can download Insominia from <https://insomnia.rest/download/core/>

If you have Insominia installed, just click on the button bellow:

[![Run in Insomnia}](https://insomnia.rest/images/run.svg)](https://insomnia.rest/run/?label=Saga%20Demo&uri=https%3A%2F%2Fraw.githubusercontent.com%2Frafabene%2Fsaga-demo%2Fmaster%2FInsomnia.json)

## Overall Architecture

![alt](architecture.png)

## Services information

The docker-compose file executes the following services

- **mongodb** - Used to store `rooms` documents.

    Port: 279017

- **mongoexpres** - Mongo UI

    Available at <http://localhost:8083/>

- **mysql** - Used to store `order` and `payment` tables. 
    
    Port: 3306

- **zookeeper** and **kafka** - Kafka Broker

    Ports: 9092 (Inside docker network) / 19092 (To connect from outside docker network)

- **kafkadrop** - Kafka Web UI 

    Available at <http://localhost:9000/>

- **order** - Written in [`NodeJS/ExpressJS`](https://expressjs.com/), Receive `order` requests and process `order`responses.

    Port: 8080
    
- **payment** - Written in [`MicroProfile/Helidon`](https://helidon.io/), Receive `payment` requests and process `payment`responses.

    Port: 8082

- **orchestrator** - Written in [`Kafka Streams`](https://kafka.apache.org/25/documentation/streams/). Coordinate the calls between the Kafka Topics from different services.

    This service doesn't expose any ports.