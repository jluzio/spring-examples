package com.example.spring.core.config_props;

import static java.lang.System.setProperty;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(ServiceProperties.class)
@EnableConfigurationProperties
@ActiveProfiles("unknown-profile")
@Slf4j
public class EnvConfigurationPropertiesTest {

  static {
    setProperty(
        "keycloak.authenticators.otp-authenticator.enabled",
        "true");
    setProperty(
        "keycloak.authenticators.otp-authenticator.communications-service-endpoint",
        "http://comms-service:8080/service-api/v1/batches");
    setProperty(
        "keycloak.authenticators.otp-authenticator.otp-channels",
        "true");
    setProperty(
        "keycloak.authenticators.otp-authenticator.otp-channels.text.channel",
        "text");
    setProperty(
        "keycloak.authenticators.otp-authenticator.otp-channels.text.from",
        "+351000000000");
    setProperty(
        "keycloak.authenticators.otp-authenticator.otp-channels.text.identity-attributes.phoneNumber",
        "1");
    setProperty(
        "keycloak.authenticators.otp-authenticator.otp-channels.email.channel",
        "email");
    setProperty(
        "keycloak.authenticators.otp-authenticator.otp-channels.email.from",
        "john.doe@server.com");
    setProperty(
        "keycloak.authenticators.otp-authenticator.otp-channels.email.identity-attributes.phoneNumber",
        "2");
  }

  @Autowired
  ServiceProperties properties;

  @Test
  void test() {
    log.info("{}", properties);
  }
}
