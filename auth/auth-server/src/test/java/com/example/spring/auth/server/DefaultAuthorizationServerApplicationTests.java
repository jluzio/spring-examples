package com.example.spring.auth.server;

import static org.assertj.core.api.Assertions.assertThat;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DefaultAuthorizationServerApplicationTests {

  private static final String REDIRECT_URI = "http://127.0.0.1:8080/login/oauth2/code/oidc-client";

  private static final String AUTHORIZATION_REQUEST = UriComponentsBuilder
      .fromPath("/oauth2/authorize")
      .queryParam("response_type", "code")
      .queryParam("client_id", "oidc-client")
      .queryParam("scope", "openid")
      .queryParam("state", "some-state")
      .queryParam("redirect_uri", REDIRECT_URI)
      .toUriString();

  @Autowired
  private WebClient webClient;

  @BeforeEach
  void setUp() {
    webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
    webClient.getOptions().setRedirectEnabled(true);
    webClient.getCookieManager().clearCookies();  // log out
  }

  @Test
  void whenLoginSuccessfulThenDisplayNotFoundError() throws IOException {
    HtmlPage page = webClient.getPage("/");

    assertLoginPage(page);

    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    WebResponse signInResponse = signIn(page, "user", "password").getWebResponse();
    assertThat(signInResponse.getStatusCode()).isEqualTo(
        HttpStatus.NOT_FOUND.value());  // there is no "default" index page
  }

  @Test
  void whenLoginFailsThenDisplayBadCredentials() throws IOException {
    HtmlPage page = webClient.getPage("/");

    HtmlPage loginErrorPage = signIn(page, "user", "wrong-password");

    HtmlElement alert = loginErrorPage.querySelector("div[role=\"alert\"]");
    assertThat(alert).isNotNull();
    assertThat(alert.getTextContent()).isEqualTo("Bad credentials");
  }

  @Test
  void whenNotLoggedInAndRequestingTokenThenRedirectsToLogin() throws IOException {
    HtmlPage page = webClient.getPage(AUTHORIZATION_REQUEST);

    assertLoginPage(page);
  }

  @Test
  void whenLoggingInAndRequestingTokenThenRedirectsToClientApplication() throws IOException {
    // Log in
    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    webClient.getOptions().setRedirectEnabled(false);
    signIn(webClient.getPage("/login"), "user", "password");

    // Request token
    WebResponse response = webClient.getPage(AUTHORIZATION_REQUEST).getWebResponse();

    assertThat(response.getStatusCode())
        .isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());
    String location = response.getResponseHeaderValue("location");
    assertThat(location)
        .startsWith(REDIRECT_URI)
        .contains("code=");
  }

  private static <P extends Page> P signIn(HtmlPage page, String username, String password) throws IOException {
    HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
    HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
    HtmlButton signInButton = page.querySelector("button");

    usernameInput.type(username);
    passwordInput.type(password);
    return signInButton.click();
  }

  private static void assertLoginPage(HtmlPage page) {
    assertThat(page.getUrl().toString()).endsWith("/login");

    HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
    HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
    HtmlButton signInButton = page.querySelector("button");

    assertThat(usernameInput).isNotNull();
    assertThat(passwordInput).isNotNull();
    assertThat(signInButton.getTextContent()).isEqualTo("Sign in");
  }

}