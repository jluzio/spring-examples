# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.5/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#using-boot-devtools)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Function](https://cloud.spring.io/spring-cloud-function/)

### Additional Links
These additional references should also help you:

* [Various sample apps using Spring Cloud Function](https://github.com/spring-cloud/spring-cloud-function/tree/master/spring-cloud-function-samples)

### Tests
curl -H "Content-Type: text/plain" localhost:8080/uppercase -d Hello
curl -H "Content-Type: text/plain" localhost:8080/lowercase -d Hello
curl -H "Content-Type: text/plain" localhost:8080/lowercase,reverse -d Hello
curl -H "Content-Type: text/plain" localhost:8080/lowercase,reverse/TestTing
curl --location --request GET 'http://localhost:8080/users'
curl --location --request GET 'http://localhost:8080/users,username'
curl --location --request POST 'http://localhost:8080/findUser,email' \
--header 'Content-Type: application/json' \
--data-raw '[
"user-1",
"user-3"
]'