package com.example.spring.core.config_props;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak.authenticators.otp-authenticator")
@Data
public class ServiceProperties {

  private Boolean enabled;
  private String communicationsServiceEndpoint;
  private Map<String, OtpChannel> otpChannels;

  @Data
  public static class OtpChannel {

    private String channel;
    private String from;
    private Map<String, String> identityAttributes;

  }
}
