package com.example.spring.messaging.kafka.core.course.opensearch;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class OpenSearchConfig {

  private final OpenSearchConfigProps openSearchConfigProps;

  @Bean
  public OpenSearchClient openSearchClient()
      throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    final HttpHost[] hosts = new HttpHost[]{
        new HttpHost(
            openSearchConfigProps.getScheme(),
            openSearchConfigProps.getHost(),
            openSearchConfigProps.getPort()
        )
    };

    var sslContext = SSLContextBuilder.create()
        .loadTrustMaterial(null, (chains, authType) -> true)
        .build();

    OpenSearchTransport transport = ApacheHttpClient5TransportBuilder
        .builder(hosts)
        .setMapper(new JacksonJsonpMapper())
        .setHttpClientConfigCallback(httpClientBuilder -> {
          final var credentialsProvider = new BasicCredentialsProvider();
          for (final var host : hosts) {
            credentialsProvider.setCredentials(
                new AuthScope(host),
                new UsernamePasswordCredentials(
                    openSearchConfigProps.getUsername(),
                    openSearchConfigProps.getPassword().toCharArray()));
          }

          // Disable SSL/TLS verification as our local testing clusters use self-signed certificates
          final var tlsStrategy = ClientTlsStrategyBuilder.create()
              .setSslContext(sslContext)
              .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
              .build();

          var connectionManager = PoolingAsyncClientConnectionManagerBuilder.create()
              .setTlsStrategy(tlsStrategy)
              .build();

          return httpClientBuilder
              .setDefaultCredentialsProvider(credentialsProvider)
              .setConnectionManager(connectionManager);
        })
        .build();
    return new OpenSearchClient(transport);
  }
}
