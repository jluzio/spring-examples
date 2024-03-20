# References
- https://spring.io/projects/spring-data
  - https://spring.io/projects/spring-data-jpa
  - https://spring.io/projects/spring-data-rest
  - https://spring.io/projects/spring-data-redis
- https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html
- http://querydsl.com

# Data Populators
## SQL
File data.sql is automatically used if present. 
Might require some configuration such as:
~~~yaml
spring:
  jpa:
    defer-datasource-initialization: true
~~~

## JSON/XML
There must be a repository populator configured.
Note that references to other entities is by using the object in the json/xml.
Example:
~~~json
{
"_class": "model.com.example.spring.data.User",
"id": 1,
"name": "John Doe",
"email": "john.doe@mail.org",
"role": { "id": 1 }
}
~~~

