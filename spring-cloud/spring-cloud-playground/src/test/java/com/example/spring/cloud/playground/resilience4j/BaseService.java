package com.example.spring.cloud.playground.resilience4j;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.vavr.control.Try;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BaseService {

  public String failure() {
    throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception");
  }

  public String ignoreException() {
    throw new BusinessException("This exception is ignored by the CircuitBreaker of service");
  }

  public String success() {
    return "Hello World from service";
  }

  public String successException() {
    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "This is a remote client exception");
  }

  public Flux<String> fluxFailure() {
    return Flux.error(new IOException("BAM!"));
  }

  public Flux<String> fluxTimeout() {
    return Flux.
        just("Hello World from service")
        .delayElements(Duration.ofSeconds(10));
  }

  public Mono<String> monoSuccess() {
    return Mono.just("Hello World from service");
  }

  public Mono<String> monoFailure() {
    return Mono.error(new IOException("BAM!"));
  }

  public Mono<String> monoTimeout() {
    return Mono.just("Hello World from service")
        .delayElement(Duration.ofSeconds(10));
  }

  public Flux<String> fluxSuccess() {
    return Flux.just("Hello", "World");
  }

  public String failureWithFallback() {
    return failure();
  }

  public CompletableFuture<String> futureSuccess() {
    return CompletableFuture.completedFuture("Hello World from service");
  }

  public CompletableFuture<String> futureFailure() {
    CompletableFuture<String> future = new CompletableFuture<>();
    future.completeExceptionally(new IOException("BAM!"));
    return future;
  }

  public CompletableFuture<String> futureTimeout() {
    Try.run(() -> Thread.sleep(5000));
    return CompletableFuture.completedFuture("Hello World from service");
  }

  protected String fallback(HttpServerErrorException ex) {
    return "Recovered HttpServerErrorException: " + ex.getMessage();
  }

  protected String fallback(Exception ex) {
    return "Recovered: " + ex.toString();
  }

  protected CompletableFuture<String> futureFallback(TimeoutException ex) {
    return CompletableFuture.completedFuture("Recovered specific TimeoutException: " + ex.toString());
  }

  protected CompletableFuture<String> futureFallback(BulkheadFullException ex) {
    return CompletableFuture.completedFuture("Recovered specific BulkheadFullException: " + ex.toString());
  }

  protected CompletableFuture<String> futureFallback(CallNotPermittedException ex) {
    return CompletableFuture.completedFuture("Recovered specific CallNotPermittedException: " + ex.toString());
  }

  protected Mono<String> monoFallback(Exception ex) {
    return Mono.just("Recovered: " + ex.toString());
  }

  protected Flux<String> fluxFallback(Exception ex) {
    return Flux.just("Recovered: " + ex.toString());
  }
}
