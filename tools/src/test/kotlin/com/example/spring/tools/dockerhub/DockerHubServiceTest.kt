package com.example.spring.tools.dockerhub

import com.example.spring.tools.dockerhub.DockerHubService.ImageTag
import com.example.spring.tools.test.TestSupport
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.EnabledIf
import java.io.UncheckedIOException

@SpringBootTest(classes = [DockerHubService::class, JacksonAutoConfiguration::class])
@EnabledIf(TestSupport.LIVE_TEST_ENABLE_RULE)
class DockerHubServiceTest {

  @Autowired
  lateinit var service: DockerHubService

  @Autowired
  lateinit var objectMapper: ObjectMapper

  val log = LoggerFactory.getLogger(this.javaClass)

  @Test
  fun findMatchingTags() = runTest {
    val namespace = "bitnami"
    val repository = "kafka"
    val targetTag = "latest"

    val tag = service.getImageTag(namespace, repository, targetTag)
    logData("tag", tag)

    val digest = tag.digest
    if (digest != null) {
      val tags = service.listImageTags(namespace, repository)
      logData("tags", tags)

      val matchingTags = service.findMatchingTags(tags, digest)
      logData("matchingTags", matchingTags)
    }
  }

  @Test
  fun testData() {
    val data = """
      [
        {
          "name": "latest",
          "digest": "sha256:e894474e39960ef66763c25e5a451dfb72d1c657533dc381ea9351877617816b",
          "tag_status": "active",
          "last_updated": "2024-07-18T15:05:24.493999Z"
        },
        {
          "name": "3.7",
          "digest": "sha256:e894474e39960ef66763c25e5a451dfb72d1c657533dc381ea9351877617816b",
          "tag_status": "active",
          "last_updated": "2024-07-18T15:05:24.017111Z"
        },
        {
          "name": "1.0",
          "digest": null,
          "tag_status": "active",
          "last_updated": "2024-07-18T15:05:24.017111Z"
        }
      ]
    """.trimIndent()

    val decodedData = objectMapper.readValue(data, Array<ImageTag>::class.java)
    logData("data", decodedData)
  }

  private fun logData(logTag: String, value: Any?) {
    try {
      log.debug("{}: {}", logTag, objectMapper.writeValueAsString(value))
    } catch (e: JsonProcessingException) {
      throw UncheckedIOException(e)
    }
  }

}