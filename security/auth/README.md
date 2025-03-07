# auth
https://docs.spring.io/spring-authorization-server/reference/overview.html

# setup
- auth-server: spring-authorization-server
- auth-resource-server: api protected by checking tokens in auth-server
- auth-client: client that logins and consumes resources from auth-resource-server
  - NOTE: seems to require login on http://127.0.0.1:8080 otherwise won't match redirect in auth-server
  - TODO: fix this issue(?)
  - http://127.0.0.1:8080 for simple login
  - http://127.0.0.1:8080/app/greetings to consume /greetings from auth-resource-server

## Refs
- spring-authorization-server: https://docs.spring.io/spring-authorization-server/reference/overview.html
- gateway: https://docs.spring.io/spring-cloud-gateway/reference/

### Samples
- https://github.com/spring-projects/spring-authorization-server/tree/main/samples
  - authorize + ui test: https://github.com/spring-projects/spring-authorization-server/blob/main/samples/default-authorizationserver/src/test/java/sample/DefaultAuthorizationServerApplicationTests.java
  - client with configured clients: 
    - https://github.com/spring-projects/spring-authorization-server/blob/main/samples/demo-client/src/main/resources/application.yml
    - certificates
      - https://github.com/spring-projects/spring-authorization-server/tree/main/samples/demo-authorizationserver/src/main/resources
      - https://github.com/spring-projects/spring-authorization-server/tree/main/samples/demo-client/src/main/resources
- https://github.com/spring-tips/spring-authorization-server
- token relay (gateway): https://github.com/spring-cloud-samples/sample-gateway-oauth2login

# Other docs
- client tutorial: https://www.youtube.com/watch?v=us0VjFiHogo
