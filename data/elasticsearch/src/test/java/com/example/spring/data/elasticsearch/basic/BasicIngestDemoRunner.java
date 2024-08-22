package com.example.spring.data.elasticsearch.basic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.example.spring.data.elasticsearch.model.Movie;
import java.io.IOException;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

public class BasicIngestDemoRunner {

  public static void main(String[] args) throws IOException {
    // URL and API key
    String serverUrl = "http://localhost:9200";
    String username = "elastic";
    String password = "changeme";

    var host = HttpHost.create(serverUrl);

    // Create the low-level client

    RestClient restClient = RestClient
        .builder(host)
        .setHttpClientConfigCallback(httpClientBuilder -> {
          var credentialsProvider = new BasicCredentialsProvider();
          credentialsProvider.setCredentials(
              new AuthScope(host),
              new UsernamePasswordCredentials(username, password)
          );
          return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        })
        .build();

    // Create the transport with a Jackson mapper
    ElasticsearchTransport transport = new RestClientTransport(
        restClient, new JacksonJsonpMapper());

    // And create the API client
    ElasticsearchClient esClient = new ElasticsearchClient(transport);

    var builder = new BulkRequest.Builder();

    var movies = List.of(
        Movie.builder()
            .title("Interstellar")
            .year("2014")
            .genres(List.of("Sci-Fi"))
            .build(),
        Movie.builder()
            .title("The Matrix")
            .year("1999")
            .genres(List.of("Sci-Fi"))
            .build()
    );

    movies.forEach(movie -> {
      builder.operations(op ->
          op.index(idx ->
              idx.index("movie-index")
                  .document(movie))
      );
    });

    // Use the client...
    BulkRequest request = builder.build();
    esClient.bulk(request);

    // Close the transport, freeing the underlying thread
    transport.close();
  }

}
