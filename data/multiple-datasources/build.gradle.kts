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
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")

  developmentOnly("org.springframework.boot:spring-boot-devtools")
  testCompileOnly("org.springframework.boot:spring-boot-devtools")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  compileOnly("org.projectlombok:lombok")
  testCompileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")
  testAnnotationProcessor("org.projectlombok:lombok")

  implementation("org.hsqldb:hsqldb")
  implementation("com.h2database:h2")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
