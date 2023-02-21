package com.example.spring.stream.loansource;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class LoanSourceApplication {

  private final List<String> names = List.of(
      "Donald", "Theresa", "Vladimir",
      "Angela", "Emmanuel", "Shinzō",
      "Jacinda", "Kim");
  private final List<Long> amounts = List.of(
      10L, 100L, 1000L,
      10000L, 100000L, 1000000L,
      10000000L, 100000000L, 100000000L);
  private final Random random = new Random();
  ;

  public static void main(String[] args) {
    SpringApplication.run(LoanSourceApplication.class, args);
    log.info("The Loansource Application has started...");
  }

  @Bean
  public Supplier<Loan> supplyLoan() {
    return () -> {
      Loan loan = new Loan(
          UUID.randomUUID().toString(),
          names.get(random.nextInt(names.size())),
          amounts.get(random.nextInt(amounts.size())));
      log.info("{} {} for ${} for {}",
          loan.getStatus(), loan.getUuid(), loan.getAmount(), loan.getName());
      return loan;
    };
  }
}
