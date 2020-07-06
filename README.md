# Saga Demo

To Execute this project, clone the repository, `cd` into it, and execute:

    docker-compose up

You can input the [Insominia file](Insomnia.json) to POST a `new order` to <http://localhost:8080/orchestration/order>

## Services information

The docker-compose file executes the following services

- **mysql** - Used to store `order` and `payment` tables. 
    
    Port: 3306

- **zookeeper** and **kafka** - Kafka Broker

    Ports: 9092 (Inside docker network) / 19092 (To connect from outside docker network)

- **kafkadrop** - Kafka Web UI 

    Available at <http://localhost:9000/>

- **order** - Written in [`NodeJS/ExpressJS`](https://expressjs.com/), Receive `order` requests and process `order`responses.

    Port: 8080
    
- **payment** - Written in [`MicroProfile/Helidon`](https://helidon.io/), Receive `payment` requests and process `payment`responses.

    Port: 8081

- **orchestrator** - Written in [`Kafka Streams`](https://kafka.apache.org/25/documentation/streams/). Coordinate the calls between the Kafka Topics from different services.

    This service doesn't expose any ports.