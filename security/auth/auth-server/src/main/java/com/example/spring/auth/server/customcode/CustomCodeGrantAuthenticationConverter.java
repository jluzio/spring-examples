package com.example.spring.auth.server.customcode;

import static com.example.spring.auth.server.customcode.CustomCodeGrantAuthenticationToken.CUSTOM_CODE_GRANT_TYPE;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.CLIENT_ID;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.CLIENT_SECRET;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.CODE;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.GRANT_TYPE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public class CustomCodeGrantAuthenticationConverter implements AuthenticationConverter {

  @Nullable
  @Override
  public Authentication convert(HttpServletRequest request) {
    // grant_type (REQUIRED)
    String grantType = request.getParameter(GRANT_TYPE);
    if (!CUSTOM_CODE_GRANT_TYPE.equals(grantType)) {
      return null;
    }

    Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

    MultiValueMap<String, String> parameters = getParameters(request);
    Map<String, Object> additionalParameters = computeAdditionalParameters(parameters);

    // code (REQUIRED)
    String code = parameters.getFirst(CODE);
    if (!StringUtils.hasText(code) ||
        parameters.get(CODE).size() != 1) {
      throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
    }

    return new CustomCodeGrantAuthenticationToken(code, clientPrincipal, additionalParameters);
  }

  private MultiValueMap<String, String> getParameters(HttpServletRequest request) {
    Map<String, String[]> parameterMap = request.getParameterMap();
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
    parameterMap.forEach((key, values) -> {
      for (String value : values) {
        parameters.add(key, value);
      }
    });
    return parameters;
  }

  private Map<String, Object> computeAdditionalParameters(MultiValueMap<String, String> parameters) {
    var baseParameterNames = List.of(GRANT_TYPE, CLIENT_ID, CODE, CLIENT_SECRET);

    Map<String, Object> additionalParameters = new HashMap<>();
    parameters.forEach((key, value) -> {
      if (!baseParameterNames.contains(key)) {
        additionalParameters.put(key, value.getFirst());
      }
    });
    return additionalParameters;
  }

}