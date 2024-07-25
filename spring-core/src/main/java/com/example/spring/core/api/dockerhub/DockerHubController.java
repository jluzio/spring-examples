package com.example.spring.core.api.dockerhub;

import com.example.spring.core.api.dockerhub.DockerHubService.ImageTag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/dockerhub")
@RequiredArgsConstructor
@Log4j2
public class DockerHubController {

  private final DockerHubService service;

  @GetMapping("/images/{namespace}/{repository}/tags")
  public Mono<List<ImageTag>> getAllTags(
      @PathVariable String namespace,
      @PathVariable String repository
  ) {
    return service.listImageTags(namespace, repository);
  }

  @GetMapping("/images/{namespace}/{repository}/tags/{tag}")
  public Mono<ImageTag> getTag(
      @PathVariable String namespace,
      @PathVariable String repository,
      @PathVariable String tag
  ) {
    return service.getImageTag(namespace, repository, tag);
  }

  @GetMapping("/images/{namespace}/{repository}/tags/matching")
  public Mono<List<ImageTag>> getMatchingTags(
      @PathVariable String namespace,
      @PathVariable String repository,
      @RequestParam String digest
  ) {
    return service.listImageTags(namespace, repository)
        .map(imageTags -> service.findMatchingTags(imageTags, digest));
  }

}
