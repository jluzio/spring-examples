package com.example.spring.core.api.dockerhub;

import com.example.spring.core.api.dockerhub.DockerHubService.ImageTag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping("/matching-tags")
  public Mono<List<ImageTag>> getMatchingTags(@RequestParam String namespace,
      @RequestParam String repository, @RequestParam String tag) {
    return Mono.zip(
        service.getImageTag(namespace, repository, tag),
        service.listImageTags(namespace, repository)
    ).map(data -> {
      var imageTag = data.getT1();
      var imageTags = data.getT2();
      return service.findMatchingTags(imageTags, imageTag.digest());
    });
  }

}
