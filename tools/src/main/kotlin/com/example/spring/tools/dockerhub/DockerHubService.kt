package com.example.spring.tools.dockerhub

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import kotlin.math.ceil

@Service
class DockerHubService {

  companion object {

    const val BASE_URL: String = "https://hub.docker.com/v2"
    const val LIST_TAGS: String = "/namespaces/{namespace}/repositories/{repository}/tags"
    const val PAGE_SIZE_PARAM: String = "page_size"
    const val PAGE_PARAM: String = "page"
    const val GET_TAG: String = "/namespaces/{namespace}/repositories/{repository}/tags/{tag}"
  }

  private val webClient: WebClient = WebClient.create(BASE_URL)

  @Throws(HttpClientErrorException::class)
  suspend fun getImageTag(namespace: String, repository: String, tag: String): ImageTag {
    val uriString = UriComponentsBuilder.fromUriString(GET_TAG)
      .buildAndExpand(namespace, repository, tag)
      .toUriString()
    return webClient.get()
      .uri(uriString)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(ImageTag::class.java)
      .awaitSingle()
  }

  @Throws(HttpClientErrorException::class)
  suspend fun listImageTags(namespace: String, repository: String): List<ImageTag> {
      val pageSize = 100
      val imageTags = mutableListOf<ImageTag>()

      val firstResult = listImageTags(namespace, repository, pageSize, 1)
      val pages = ceil(firstResult.tag.count.toFloat() / pageSize).toInt()
      imageTags.addAll(firstResult.tag.results)

      for (page in 2..pages) {
        val pagedResult = listImageTags(namespace, repository, pageSize, page)
        imageTags.addAll(pagedResult.tag.results)
      }

      return imageTags
    }

  private suspend fun listImageTags(
    namespace: String, repository: String, pageSize: Int,
    page: Int
  ): PagedImageTagList {
    val uriString = UriComponentsBuilder.fromUriString(LIST_TAGS)
      .queryParam(PAGE_SIZE_PARAM, pageSize)
      .queryParam(PAGE_PARAM, page)
      .buildAndExpand(namespace, repository)
      .toUriString()
    return webClient.get()
      .uri(uriString)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(ImageTagList::class.java)
      .map { tag -> PagedImageTagList(tag, page) }
      .awaitSingle()
  }

  fun findMatchingTags(tags: List<ImageTag>, digest: String): List<ImageTag> {
    return tags.stream()
      .filter { tag: ImageTag -> tag.digest == digest }
      .toList()
  }

  data class ImageTag(
    val name: String,
    val digest: String?,
    @field:JsonProperty("tag_status") val tagStatus: String,
    @field:JsonProperty("last_updated") val lastUpdated: String
  )

  data class ImageTagList(val count: Int, val results: List<ImageTag>)

  data class PagedImageTagList(val tag: ImageTagList, val page: Int)

}