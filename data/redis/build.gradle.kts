plugins {
  java
  id("org.springframework.boot") version "3.4.2"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example.spring"
version = "1.0.0"

java {
  sourceCompatibility = JavaVersion.VERSION_21
}

// enable Java preview features
val compileJvmArgs = emptyList<String>()
//val compileJvmArgs = listOf("--enable-preview")
val runtimeJvmArgs = listOf(
//  "--enable-preview",
  "--add-opens", "java.base/java.lang=ALL-UNNAMED",
  "--add-opens", "java.base/java.util=ALL-UNNAMED"
)
tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.addAll(compileJvmArgs)
}
tasks.withType<Test>().configureEach {
  jvmArgs(runtimeJvmArgs)
}
tasks.withType<JavaExec>().configureEach {
  jvmArgs(runtimeJvmArgs)
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-data-rest")
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("org.springframework.retry:spring-retry")

  developmentOnly("org.springframework.boot:spring-boot-devtools")
  testCompileOnly("org.springframework.boot:spring-boot-devtools")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  compileOnly("org.projectlombok:lombok")
  testCompileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")
  testAnnotationProcessor("org.projectlombok:lombok")
  // NOTE: Docker Composed Support enforces a required docker-compose file if spring.docker.compose.enabled is true (which is by default)
  developmentOnly("org.springframework.boot:spring-boot-docker-compose")
  testImplementation("org.springframework.boot:spring-boot-docker-compose")

  implementation("org.hsqldb:hsqldb")
  implementation("redis.clients:jedis")
  implementation("io.lettuce:lettuce-core")
  implementation("com.google.guava:guava:33.0.0-jre")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-testcontainers")
  testImplementation("com.redis:testcontainers-redis:2.2.2")
  testImplementation("org.testcontainers:junit-jupiter")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
