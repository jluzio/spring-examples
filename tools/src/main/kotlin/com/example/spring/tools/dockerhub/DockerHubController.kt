package com.example.spring.tools.dockerhub

import com.example.spring.tools.dockerhub.DockerHubService.ImageTag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dockerhub")
class DockerHubController(private val service: DockerHubService) {

  @GetMapping("/images/{namespace}/{repository}/tags")
  suspend fun getAllTags(
    @PathVariable namespace: String,
    @PathVariable repository: String
  ): List<ImageTag> {
    return service.listImageTags(namespace, repository)
  }

  @GetMapping("/images/{namespace}/{repository}/tags/{tag}")
  suspend fun getTag(
    @PathVariable namespace: String,
    @PathVariable repository: String,
    @PathVariable tag: String
  ): ImageTag {
    return service.getImageTag(namespace, repository, tag);
  }

  @GetMapping("/images/{namespace}/{repository}/tags/matching")
  suspend fun getMatchingTags(
    @PathVariable namespace: String,
    @PathVariable repository: String,
    @RequestParam digest: String
  ): List<ImageTag> {
    val imageTags = service.listImageTags(namespace, repository)
    return service.findMatchingTags(imageTags, digest)
  }
}