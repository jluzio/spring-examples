plugins {
  java
  id("org.springframework.boot") version "3.5.4"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example.spring"
version = "1.0.0"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(25)
  }
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
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
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
  testRuntimeOnly("org.springframework.boot:spring-boot-docker-compose")

  implementation("com.google.guava:guava:33.2.1-jre")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-testcontainers")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:mongodb")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
