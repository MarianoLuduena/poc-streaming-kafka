# Streaming Kafka - Proof of Concept

## Architecture

```
                                                                              ,.-^^-._ 
                                                                             |-.____.-|
                                                                             |        |
                                                                             |        |
     ┌──────┐          ┌───┐          ┌─────┐          ┌─────────┐           |        |
     │Client│          │App│          │Kafka│          │JDBC Sink│           '-.____.-'
     └──┬───┘          └─┬─┘          └──┬──┘          └────┬────┘           Postgres  
        │     Person     │               │                  │                   │      
        │ ──────────────>│               │                  │                   │      
        │                │               │                  │                   │      
        │                │    Person     │ ╔═══════════════╗│                   │      
        │                │ ─ ─ ─ ─ ─ ─ ─>│ ║Sent to topic ░║│                   │      
        │                │               │ ║'person'       ║│                   │      
        │                │               │ ╚═══════════════╝│                   │      
        │                │               │                  │                   │      
        │ <──────────────│               │                  │                   │      
        │                │               │                  │                   │      
        │                │     ACK       │                  │                   │      
        │                │<─ ─ ─ ─ ─ ─ ─ │                  │                   │      
        │                │               │                  │                   │      
        │                │               │ Consume message  │                   │      
        │                │               │─────────────────>│                   │      
        │                │               │                  │                   │      
        │                │              ╔╧════════════════╗ │  Insert message   │      
        │                │              ║Read from topic ░║ │──────────────────>│      
        │                │              ║'person'         ║ │                   │      
        │                │              ╚╤════════════════╝ │                   │      
        │                │               │                  │                   │      
        │                │               │                  │<──────────────────│      
        │                │               │                  │                   │      
        │                │               │       ACK        │                   │      
        │                │               │<─────────────────│                   │      
     ┌──┴───┐          ┌─┴─┐          ┌──┴──┐          ┌────┴────┐           Postgres  
     │Client│          │App│          │Kafka│          │JDBC Sink│            ,.-^^-._ 
     └──────┘          └───┘          └─────┘          └─────────┘           |-.____.-|
                                                                             |        |
                                                                             |        |
                                                                             |        |
                                                                             '-.____.-'

```

## Setup (locally)

Start up Kafka and satellite apps:

```shell
docker compose up -d
```

*NOTE*: It might be necessary to add the rule `127.0.0.1	kafka` to the `/etc/hosts` file so as to be able to access the advertised Kafka broker.

Then, create topic `person` in Kafka (this can be easily done from the UI).

Finally, create a JDBC Sink Connector to read from the topic `person` and to write to the Postgres DB:

```shell
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d '{
"name": "jdbc_sink_pg_person",
"config": {
        "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
        "key.converter.schema.registry.url": "http://schema-registry:8081",
        "value.converter": "io.confluent.connect.avro.AvroConverter",
        "value.converter.schema.registry.url": "http://schema-registry:8081",
        "connection.url": "jdbc:postgresql://poc_postgres:5432/postgres?currentSchema=public",
        "connection.user": "postgres",
        "connection.password": "password",
        "topics": "person",
        "mode":"bulk",
        "schemas.enabled": true,
        "transforms": "BirthDateConverter",
        "transforms.BirthDateConverter.type": "org.apache.kafka.connect.transforms.TimestampConverter$Value",
        "transforms.BirthDateConverter.field": "birth_date",
        "transforms.BirthDateConverter.format": "yyyy-MM-dd",
        "transforms.BirthDateConverter.target.type": "Date"
    }
}'
```

## Testing the app (locally)

Spin up the sandbox app and produce messages via the REST API it provides:

```shell
curl -X POST 'http://localhost:8080/messages' \
--header 'Content-Type: application/json' \
--data '{
    "document_type": "RUT",
    "document_number": "159439704",
    "first_name": "Ross",
    "surname": "Kozey",
    "birth_date": "1970-01-01"
}'
```

## Links

- https://developer.confluent.io/learn-kafka/kafka-connect/docker-containers/
- https://github.com/confluentinc/cp-all-in-one/blob/v7.3.1/cp-all-in-one/docker-compose.yml
- https://docs.confluent.io/kafka-connectors/self-managed/userguide.html#standalone-mode
- https://docs.confluent.io/kafka-connectors/jdbc/current/sink-connector/overview.html#install-the-connector-manually
- https://www.confluent.io/blog/kafka-connect-deep-dive-jdbc-source-connector/
- https://www.confluent.io/blog/kafka-connect-deep-dive-converters-serialization-explained/#configuring-converters
