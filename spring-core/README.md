# References
- https://docs.spring.io/spring-boot/docs/current/reference/html/
- https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html

# Changes in Spring Boot 3.0.0

## Switch to Jakarta EE

## Auto Configuration
- Previous:
> /META-INF/spring.factories
> ~~~
> org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
> com.example.spring.framework.config.auto_configuration.SampleAutoConfiguration
> ~~~

- Current: 
> /META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
> ~~~
> com.example.spring.framework.config.auto_configuration.SampleAutoConfiguration
> ~~~

## WireMock
[2022/12/15]: Stable versions of WireMock related libraries aren't compatible with Spring Boot 3.x.
The workaround is to include unstable versions.

### Libraries affected:
> - wiremock-jre8
> - spring-cloud-starter-contract-stub-runner

### Workaround
Using Maven profile *wiremock-temp-springboot3*, which includes *wiremock-jre8-standalone* (note the standalone) and an unstable *spring-cloud-starter-contract-stub-runner*.

TODO:
- when there is a stable release compatible:
  * delete the profile *wiremock-temp-springboot3* 
  * uncomment default libraries and update to the updated versions