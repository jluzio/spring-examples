plugins {
  java
  id("org.springframework.boot") version "3.2.2"
  id("io.spring.dependency-management") version "1.1.4"
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
  val querydsl_version = "5.0.0"

  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-data-rest")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("org.springframework.retry:spring-retry")

  developmentOnly("org.springframework.boot:spring-boot-devtools")
  testCompileOnly("org.springframework.boot:spring-boot-devtools")
  developmentOnly("org.springframework.boot:spring-boot-docker-compose")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  compileOnly("org.projectlombok:lombok")
  testCompileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")
  testAnnotationProcessor("org.projectlombok:lombok")
  annotationProcessor("com.querydsl:querydsl-apt:$querydsl_version:jakarta") {
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
  }

  implementation("org.hsqldb:hsqldb")
  implementation("redis.clients:jedis")
  implementation("io.lettuce:lettuce-core")
  implementation("com.querydsl:querydsl-jpa:$querydsl_version:jakarta")
  implementation("com.google.guava:guava:33.0.0-jre")
  implementation("com.github.java-json-tools:json-patch:1.13")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-testcontainers")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:mysql")
  testImplementation("com.mysql:mysql-connector-j:8.3.0")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
