package com.example.spring.tools.dockerhub

import com.example.spring.tools.dockerhub.DockerHubService.ImageTag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dockerhub")
class DockerHubController(private val service: DockerHubService) {
  @GetMapping("/tags")
  suspend fun getAllTags(
    @RequestParam namespace: String,
    @RequestParam repository: String
  ): List<ImageTag> {
    return service.listImageTags(namespace, repository)
  }

  @GetMapping("/tags/matching")
  suspend fun getMatchingTags(
    @RequestParam namespace: String,
    @RequestParam repository: String,
    @RequestParam tag: String
  ): List<ImageTag> {
    val imageTag = service.getImageTag(namespace, repository, tag);
    if (imageTag.digest == null) {
      return emptyList()
    }

    val imageTags = service.listImageTags(namespace, repository)
    return service.findMatchingTags(imageTags, imageTag.digest)
  }
}