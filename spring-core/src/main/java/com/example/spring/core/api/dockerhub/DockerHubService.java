package com.example.spring.core.api.dockerhub;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DockerHubService {

  public static final String BASE_URL = "https://hub.docker.com/v2";
  public static final String LIST_TAGS = "/namespaces/{namespace}/repositories/{repository}/tags";
  public static final String PAGE_SIZE_PARAM = "page_size";
  public static final String PAGE_PARAM = "page";
  public static final String GET_TAG = "/namespaces/{namespace}/repositories/{repository}/tags/{tag}";

  private final WebClient webClient = WebClient.builder()
      .baseUrl(BASE_URL)
      .exchangeStrategies(
          // max 2MB for transfers
          ExchangeStrategies.builder()
              .codecs(it -> it.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
              .build()
      )
      .build();

  public Mono<ImageTag> getImageTag(String namespace, String repository, String tag)
      throws HttpClientErrorException {
    var uriString = UriComponentsBuilder.fromUriString(GET_TAG)
        .buildAndExpand(namespace, repository, tag)
        .toUriString();
    return webClient.get()
        .uri(uriString)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(ImageTag.class);
  }

  public Mono<List<ImageTag>> listImageTags(String namespace, String repository)
      throws HttpClientErrorException {
    int pageSize = 100;

    return listImageTags(namespace, repository, pageSize, 1)
        .expand(pagedImageTagList -> {
          int pages = (int) Math.ceil(pagedImageTagList.tag().count() / pageSize);
          if (pagedImageTagList.page() < pages) {
            return listImageTags(namespace, repository, pageSize, pagedImageTagList.page() + 1);
          } else {
            return Mono.empty();
          }
        })
        .reduce(
            new ArrayList<>(),
            (imageTags, pagedImageTagList) -> {
              imageTags.addAll(pagedImageTagList.tag().results());
              return imageTags;
            });
  }

  private Mono<PagedImageTagList> listImageTags(String namespace, String repository, int pageSize,
      int page) {
    var uriString = UriComponentsBuilder.fromUriString(LIST_TAGS)
        .queryParam(PAGE_SIZE_PARAM, pageSize)
        .queryParam(PAGE_PARAM, page)
        .buildAndExpand(namespace, repository)
        .toUriString();
    return webClient.get()
        .uri(uriString)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(ImageTagList.class)
        .map(tag -> new PagedImageTagList(tag, page));
  }

  public List<ImageTag> findMatchingTags(List<ImageTag> tags, String digest) {
    return tags.stream()
        .filter(tag -> Objects.equals(tag.digest(), digest))
        .toList();
  }

  public record ImageTag(String name, String digest, @JsonProperty("tag_status") String tagStatus,
                         @JsonProperty("last_updated") String lastUpdated) {

  }

  public record ImageTagList(int count, List<ImageTag> results) {

  }

  public record PagedImageTagList(ImageTagList tag, int page) {

  }

}
