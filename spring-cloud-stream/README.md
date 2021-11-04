## Environment

These event driven applications are built on: [Spring Boot][boot], [Spring Cloud Stream][stream].

This server-side runs on [Docker][docker] and includes: [Kafka][kafka], [Zookeeper][zookeeper], [RabbitMQ][rabbit], and [KafDrop][kafdrop] (image by by Obsidian Dynamics).

## Management UIs
- [Kafka][kafka-mng]
- [Rabbit][rabbit-mng]


[recipe]: https://benwilcock.github.io/spring-cloud-stream-demo/
[stream-docs]: https://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/
[boot]: https://spring.io/projects/spring-boot
[stream]: https://spring.io/projects/spring-cloud-stream
[maven]: https://maven.apache.org/
[java]: https://adoptopenjdk.net/
[docker]: https://www.docker.com/
[kafka]: https://kafka.apache.org/
[zookeeper]: https://zookeeper.apache.org/
[rabbit]: https://www.rabbitmq.com/
[kafdrop]: https://hub.docker.com/r/obsidiandynamics/kafdrop

[kafka-mng]: http://localhost:9000
[rabbit-mng]: http://localhost:15672
