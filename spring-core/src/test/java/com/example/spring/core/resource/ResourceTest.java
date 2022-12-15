package com.example.spring.core.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;

@SpringBootTest(classes = {})
@Slf4j
class ResourceTest implements ResourceLoaderAware {

  @Autowired
  ApplicationContext context;
  @Autowired
  ResourceLoader resourceLoader;
  boolean awareInit = false;

  @Test
  void test_url() throws IOException {
    var resource = new UrlResource("https://www.wikipedia.org/");
    displayResource(resource, DisplayResourceParams.defaults());

    var someProjectFile = new File(System.getProperty("user.dir"), "pom.xml");
    var resourceFile = new UrlResource(someProjectFile.toURI());
    displayResource(resourceFile, DisplayResourceParams.builder()
        .resourceId("file")
        .build());
  }

  @Test
  void test_file() throws IOException {
    var someProjectFile = new File(System.getProperty("user.dir"), "pom.xml");
    var resourceFile = new FileSystemResource(someProjectFile);
    displayResource(resourceFile, DisplayResourceParams.defaults());
  }

  @Test
  void test_classpath() throws IOException {
    var resource = new ClassPathResource("contexts/applicationContext-xmlAndAnnotations.xml");
    displayResource(resource, DisplayResourceParams.defaults());

    var resourceClass = new ClassPathResource(String.format("%s.class", getClass().getSimpleName()),
        getClass());
    displayResource(
        resourceClass,
        DisplayResourceParams.builder()
            .resourceId("class")
            .getContents(false)
            .build());
  }

  @Test
  void test_inputStream() throws IOException {
    var cpResource = new ClassPathResource("contexts/applicationContext-xmlAndAnnotations.xml");
    var resource = new InputStreamResource(cpResource.getInputStream());
    displayResource(resource, DisplayResourceParams.defaults());
  }

  @Test
  void test_byteArray() throws IOException {
    var resource = new ByteArrayResource("hello world".getBytes());
    displayResource(resource, DisplayResourceParams.defaults());
  }

  @Test
  void test_servletContext() throws IOException {
    // see ServletContextResource in a web project
  }

  @Test
  void test_resourceLoader() throws IOException {
    log.info("ctx: {} | loader: {}", context, resourceLoader);
    var resource = resourceLoader
        .getResource("classpath:/contexts/applicationContext-xmlAndAnnotations.xml");
    displayResource(resource, DisplayResourceParams.defaults());

    log.info("prefixes: classpath: | http[s]: | file: | [none] - depends on ApplicationContext");
  }

  void displayResource(@lombok.NonNull Resource resource,
      @lombok.NonNull DisplayResourceParams params) throws IOException {
    var resourceCtx = Optional.ofNullable(params.getResourceId())
        .map(id -> String.format("%s :: ", id))
        .orElse("");
    log.info("{}resource: {}", resourceCtx, resource);
    log.info("{}exists: {}", resourceCtx, resource.exists());
    if (params.isGetContents()) {
      try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
        String text = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        log.info("{}text: {}", resourceCtx, text);
      }
    }
  }

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    if (!awareInit) {
      log.info("resourceLoader: {}", resourceLoader);

    }
    awareInit = true;
  }

  @Configuration
  static class Config {

  }

  @Value
  @Builder
  static class DisplayResourceParams {

    @Builder.Default
    private String resourceId = null;
    @Builder.Default
    private boolean getContents = true;

    public static DisplayResourceParams defaults() {
      return DisplayResourceParams.builder().build();
    }

  }

}
