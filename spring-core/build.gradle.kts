plugins {
  java
  id("org.springframework.boot") version "3.2.2"
  id("io.spring.dependency-management") version "1.1.4"
	id("org.jsonschema2pojo") version "1.2.1"
}

group = "com.example.spring"
version = "1.0.0"

java {
  sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-cache")
  implementation("org.springframework.retry:spring-retry")

  developmentOnly("org.springframework.boot:spring-boot-devtools")
  developmentOnly("org.springframework.boot:spring-boot-docker-compose")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  compileOnly("org.projectlombok:lombok")
  testCompileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")
  testAnnotationProcessor("org.projectlombok:lombok")

  implementation("org.ehcache:ehcache:3.10.8")
  implementation("javax.cache:cache-api")
  implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
  implementation("jakarta.interceptor:jakarta.interceptor-api:2.1.0")
  implementation("com.google.guava:guava:33.0.0-jre")
  implementation("org.apache.commons:commons-lang3")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.projectreactor:reactor-test")
  /* WARNING:
     WireMock 3.3.1 has a dependency mismatch with Spring Boot 3.2.2.
     Current workaround is to use wiremock-standalone and exclude wiremock in wiremock-spring-boot.
   */
//	implementation("org.wiremock:wiremock:3.3.1")
	testImplementation("org.wiremock:wiremock-standalone:3.3.1")
  testImplementation("com.maciejwalkowiak.spring:wiremock-spring-boot:2.1.1") {
		exclude("org.wiremock", "wiremock")
	}
	testImplementation("uk.org.webcompere:system-stubs-core:2.1.6")
	testImplementation("uk.org.webcompere:system-stubs-jupiter:2.1.6")
}

jsonSchema2Pojo {
  sourceFiles = files("src/main/resources/schema")
  targetPackage = "com.example.types"
  generateBuilders = true
  dateType = "java.time.LocalDate"
  dateTimeType = "java.time.OffsetDateTime"
  timeType = "java.time.LocalTime"
}

tasks.withType<Test> {
  useJUnitPlatform()
}
