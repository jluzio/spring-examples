package com.example.spring.auth.server.customcode;

import jakarta.annotation.Nullable;
import java.util.Map;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.Assert;

@Getter
public class CustomCodeGrantAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

  public static final String CUSTOM_CODE_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:custom_code";
  private final String code;

  public CustomCodeGrantAuthenticationToken(
      String code,
      Authentication clientPrincipal,
      @Nullable Map<String, Object> additionalParameters
  ) {
    super(new AuthorizationGrantType(CUSTOM_CODE_GRANT_TYPE), clientPrincipal, additionalParameters);
    Assert.hasText(code, "code cannot be empty");
    this.code = code;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CustomCodeGrantAuthenticationToken test)) {
      return false;
    }
    return new EqualsBuilder()
        .appendSuper(super.equals(obj))
        .append(code, test.code)
        .build();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .appendSuper(super.hashCode())
        .append(code)
        .build();
  }
}
