package com.example.spring.cloud.playground.feign;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimbusds.jose.jwk.JWKSet;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootTest
@Slf4j
class ConvertersTest {

  @Configuration
  @Import({FeignTestConfig.class, AzureAdResponseDecoder.class})
  @EnableFeignClients(clients = AzureAdFeignClient.class)
  static class Config {

  }

  @FeignClient(name = "azure-ad")
  public interface AzureAdFeignClient {

    @GetMapping(path = "/common/discovery/v2.0/keys")
    Map<String, Object> keysAsMap();

    @GetMapping(path = "/common/discovery/v2.0/keys", produces = MediaType.APPLICATION_JSON_VALUE)
    JWKSet keysAsJwkSet();
  }

  @Component
  static class AzureAdResponseDecoder implements Decoder {

    private final Decoder delegate;

    AzureAdResponseDecoder(ObjectFactory<HttpMessageConverters> httpMessageConvertersObjectFactory) {
      delegate = new SpringDecoder(httpMessageConvertersObjectFactory);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
      if (type.getTypeName().equals(JWKSet.class.getTypeName())) {
        return decodeJwkSet(response);
      }
      return delegate.decode(response, type);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private JWKSet decodeJwkSet(Response response) throws IOException, DecodeException {
      try {
        Map<String, Object> decoded = (Map<String, Object>) delegate.decode(response, Map.class);
        return JWKSet.parse(decoded);
      } catch (ParseException e) {
        throw new DecodeException(400, "unable to decode to JWKSet", response.request(), e);
      }
    }
  }

  @Autowired
  AzureAdFeignClient api;

  @Test
  void test() {
    assertThat(api.keysAsMap())
        .isNotNull()
        .satisfies(it -> log.debug("map: {}", it));
    assertThat(api.keysAsJwkSet())
        .isNotNull()
        .satisfies(it -> log.debug("jwkSet: {}", it));
  }

}
