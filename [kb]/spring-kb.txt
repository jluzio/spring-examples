# Spring Framework
https://docs.spring.io/spring/docs/current/spring-framework-reference/pdf/
https://docs.spring.io/spring/docs/current/spring-framework-reference/


# Spring Libs

## Spring Boot
https://docs.spring.io/spring-boot/docs/current/reference/pdf/
https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/


## Spring Security
https://docs.spring.io/spring-security/site/docs/5.3.2.RELEASE/reference/pdf/
https://docs.spring.io/spring-security/site/docs/5.3.2.RELEASE/reference/html5/
https://docs.spring.io/spring-security/site/docs/5.3.2.RELEASE/api/


## Debug with Spring Boot with Maven
VM Options: -Dspring-boot.run.fork=false
Environment variables: overrides to Spring Boot app


## Running with different properties
- spring.config.location=<csv-resources>
- spring.config.additional-location=<csv-additional-resources>
- use <app-working-directory>/config


## spring-boot:run with profiles and other parameters
mvn spring-boot:run -Dspring-boot.run.profiles=<profile1>,<profile2> -Dspring-boot.run.jvmArguments="-Dspring.config.additional-location=.settings/config/"


## Profiles
- active profiles
spring.profiles.active

- Change configuration depending on the environment
A YAML file is actually a sequence of documents separated by --- lines, and each document is parsed separately to a flattened map.
If a YAML document contains a spring.profiles key, then the profiles value (comma-separated list of profiles) is fed into the Spring Environment.acceptsProfiles() and if any of those profiles is active that document is included in the final merge (otherwise not).

Example:
server:
    port: 9000
---
spring:
    profiles: development
server:
    port: 9001
---
spring:
    profiles: production
server:
    port: 0
    
In this example the default port is 9000, but if the Spring profile “development” is active then the port is 9001, and if “production” is active then it is 0.
The YAML documents are merged in the order they are encountered (so later values override earlier ones).
To do the same thing with properties files you can use application-${profile}.properties to specify profile-specific values.    

## Relaxed binding for Configuration and Environment Properties
@ConfigurationProperties(prefix="acme.my-project.person")
public class OwnerProperties {
 private String firstName;
 public String getFirstName() {
 return this.firstName;
 }
 public void setFirstName(String firstName) {
 this.firstName = firstName;
 }
}

-- Options
acme.my-project.person.first-name: Kebab case, which is recommended for use in .properties and .yml files.
acme.myProject.person.firstName: Standard camel case syntax.
acme.my_project.person.first_name: Underscore notation, which is an alternative format for use in .properties and .yml files.
ACME_MYPROJECT_PERSON_FIRSTNAME: Upper case format, which is recommended when using system enviroment.


## Spring Boot jar archive
Can be unzipped with unzip or 7zip specifying "Open Archive > zip"
Can also be less'ed to view header info.


## Doc Notes

### 1.10.1. @Component and Further Stereotype Annotations
The @Repository annotation is a marker for any class that fulfills the role or stereotype of a
repository (also known as Data Access Object or DAO). Among the uses of this marker is the
automatic translation of exceptions, as described in Exception Translation.
Spring provides further stereotype annotations: @Component, @Service, and @Controller. @Component is
a generic stereotype for any Spring-managed component. @Repository, @Service, and @Controller are
specializations of @Component for more specific use cases (in the persistence, service, and
presentation layers, respectively). Therefore, you can annotate your component classes with
@Component, but, by annotating them with @Repository, @Service, or @Controller instead, your classes
are more properly suited for processing by tools or associating with aspects. For example, these
stereotype annotations make ideal targets for pointcuts. @Repository, @Service, and @Controller can
also carry additional semantics in future releases of the Spring Framework. Thus, if you are
choosing between using @Component or @Service for your service layer, @Service is clearly the better
choice. Similarly, as stated earlier, @Repository is already supported as a marker for automatic
exception translation in your persistence layer.