## Environment

These event driven applications are built on: [Spring Boot][boot], [Spring Cloud Stream][stream-docs] (data documentation [here][stream-docs-data]).

This server-side runs on [Docker][docker] and includes: [Kafka][kafka], [Zookeeper][zookeeper], [RabbitMQ][rabbit], and [KafDrop][kafdrop] (image by by Obsidian Dynamics).

## Management UIs
- [Kafka][kafka-mng]
- [Rabbit][rabbit-mng] - user: `guest` | pass: `guest`

## Guides
- [Guide 01][guide-01] 
- [Guide 02][guide-02] 

[stream-docs]: https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/
[stream-docs-data]: https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream.html#_programming_model
[boot]: https://spring.io/projects/spring-boot
[stream]: https://spring.io/projects/spring-cloud-stream
[maven]: https://maven.apache.org
[java]: https://adoptopenjdk.net
[docker]: https://www.docker.com
[kafka]: https://kafka.apache.org
[zookeeper]: https://zookeeper.apache.org
[rabbit]: https://www.rabbitmq.com

[zookeper-docker]: https://hub.docker.com/_/zookeeper
[cp-zookeper-docker]: https://hub.docker.com/r/confluentinc/cp-zookeeper

[kafka-docker]: https://hub.docker.com/r/bitnami/kafka
[cp-kafka-docker]: https://hub.docker.com/r/confluentinc/cp-kafka
[kafdrop-docker]: https://hub.docker.com/r/obsidiandynamics/kafdrop
[kafdrop]: https://hub.docker.com/r/obsidiandynamics/kafdrop

[rabbit-docker]: https://hub.docker.com/_/rabbitmq

[guide-01]: https://benwilcock.github.io/spring-cloud-stream-demo
[guide-02]: https://medium.com/geekculture/spring-cloud-streams-with-functional-programming-model-93d49696584c

[kafka-mng]: http://localhost:9000
[rabbit-mng]: http://localhost:15672
