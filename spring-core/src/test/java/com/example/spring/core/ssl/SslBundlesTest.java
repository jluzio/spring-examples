package com.example.spring.core.ssl;

import java.security.KeyStoreException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = SslAutoConfiguration.class)
@ActiveProfiles("ssl-bundles")
@Log4j2
class SslBundlesTest {

  // ref: https://docs.spring.io/spring-boot/reference/features/ssl.html

  @Autowired
  SslBundles sslBundles;
  @Value("${spring.ssl.bundle.pem.my-pem-bundle.truststore.certificate}")
  String certificateValue;

  @Test
  void validateConfig() {
    log.debug(certificateValue);
  }

  @Test
  void test_pem() throws KeyStoreException {
    log.debug(sslBundles.getBundleNames());

    SslBundle myPemBundle = sslBundles.getBundle("my-pem-bundle");
    log.debug(myPemBundle.getStores().getTrustStore().aliases());

    SslBundle myPemBase64Bundle = sslBundles.getBundle("my-pem-base64-bundle");
    log.debug(myPemBase64Bundle.getStores().getTrustStore().aliases());

    SslBundle myPemFileBundle = sslBundles.getBundle("my-pem-file-bundle");
    log.debug(myPemFileBundle.getStores().getTrustStore().aliases());
  }

}
