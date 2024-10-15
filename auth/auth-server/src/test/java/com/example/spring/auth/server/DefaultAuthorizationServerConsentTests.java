package com.example.spring.auth.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DefaultAuthorizationServerConsentTests {

  @Autowired
  private WebClient webClient;

  @MockBean
  private OAuth2AuthorizationConsentService authorizationConsentService;

  private final String redirectUri = "http://127.0.0.1/login/oauth2/code/messaging-client";

  private final String authorizationRequestUri = UriComponentsBuilder
      .fromPath("/oauth2/authorize")
      .queryParam("response_type", "code")
      .queryParam("client_id", "messaging-client")
      .queryParam("scope", "openid message.read message.write")
      .queryParam("state", "state")
      .queryParam("redirect_uri", redirectUri)
      .toUriString();

  @BeforeEach
  void setUp() {
    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    webClient.getOptions().setRedirectEnabled(true);
    webClient.getCookieManager().clearCookies();
    when(authorizationConsentService.findById(any(), any())).thenReturn(null);
  }

  @Test
  @WithMockUser("user")
  void whenUserConsentsToAllScopesThenReturnAuthorizationCode() throws IOException {
    final HtmlPage consentPage = webClient.getPage(authorizationRequestUri);
    assertThat(consentPage.getTitleText()).isEqualTo("Consent required");

    List<HtmlCheckBoxInput> scopes = new ArrayList<>();
    consentPage.querySelectorAll("input[name='scope']").forEach(scope ->
        scopes.add((HtmlCheckBoxInput) scope));
    for (HtmlCheckBoxInput scope : scopes) {
      scope.click();
    }

    List<String> scopeIds = new ArrayList<>();
    scopes.forEach(scope -> {
      assertThat(scope.isChecked()).isTrue();
      scopeIds.add(scope.getId());
    });
    assertThat(scopeIds).containsExactlyInAnyOrder("message.read", "message.write");

    DomElement submitConsentButton = consentPage.querySelector("button[id='submit-consent']");
    webClient.getOptions().setRedirectEnabled(false);

    WebResponse approveConsentResponse = submitConsentButton.click().getWebResponse();
    assertThat(approveConsentResponse.getStatusCode())
        .isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());
    String location = approveConsentResponse.getResponseHeaderValue("location");
    assertThat(location)
        .startsWith(redirectUri)
        .contains("code=");
  }

  @Test
  @WithMockUser("user")
  void whenUserCancelsConsentThenReturnAccessDeniedError() throws IOException {
    final HtmlPage consentPage = webClient.getPage(authorizationRequestUri);
    assertThat(consentPage.getTitleText()).isEqualTo("Consent required");

    DomElement cancelConsentButton = consentPage.querySelector("button[id='cancel-consent']");
    webClient.getOptions().setRedirectEnabled(false);

    WebResponse cancelConsentResponse = cancelConsentButton.click().getWebResponse();
    assertThat(cancelConsentResponse.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());
    String location = cancelConsentResponse.getResponseHeaderValue("location");
    assertThat(location)
        .startsWith(redirectUri)
        .contains("error=access_denied");
  }

}