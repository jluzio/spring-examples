package com.example.spring.core.api.dockerhub;

import com.example.spring.core.test.TestSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UncheckedIOException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@SpringBootTest(classes = {DockerHubService.class, JacksonAutoConfiguration.class})
@Log4j2
@EnabledIf(TestSupport.LIVE_TEST_ENABLE_RULE)
class DockerHubServiceTest {

  @Autowired
  DockerHubService service;
  @Autowired
  ObjectMapper objectMapper;

  @Test
  void findMatchingTags() {
    String namespace = "bitnami";
    String repository = "kafka";
    String targetTag = "latest";

    var tag = service.getImageTag(namespace, repository, targetTag).block();
    logData("tag", tag);

    var tags = service.listImageTags(namespace, repository).block();
    logData("tags", tags);

    var matchingTags = service.findMatchingTags(tags, tag.digest());
    logData("matchingTags", matchingTags);
  }

  private void logData(String logTag, Object object) {
    try {
      log.debug("{}: {}", logTag, objectMapper.writeValueAsString(object));
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

}
