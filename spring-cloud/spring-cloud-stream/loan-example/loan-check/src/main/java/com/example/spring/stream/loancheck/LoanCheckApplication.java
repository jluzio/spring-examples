package com.example.spring.stream.loancheck;

import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootApplication
@Slf4j
public class LoanCheckApplication {

  private static final Long MAX_AMOUNT = 10000L;

  public static void main(String[] args) {
    SpringApplication.run(LoanCheckApplication.class, args);
    log.info("The Loancheck Application has started...");
  }

  @Bean
  Function<Loan, Message<Loan>> loanChecker(StreamBridge streamBridge) {
    return loan -> {
      log.info("{} {} for ${} for {}", loan.getStatus(), loan.getUuid(), loan.getAmount(),
          loan.getName());

      String sendToDestination = "";
      if (loan.getAmount() > MAX_AMOUNT) {
        loan.setStatus(Statuses.DECLINED.name());
//        streamBridge.send("loanDeclined", loan);
        sendToDestination = "loanDeclined";
      } else {
        loan.setStatus(Statuses.APPROVED.name());
        sendToDestination = "loanApproved";
      }

      log.info("{} {} for ${} for {}",
          loan.getStatus(), loan.getUuid(), loan.getAmount(), loan.getName());

      // There are at least 2 options to send message:
      // - using dynamic destination header
      // - using streamBridge

//      streamBridge.send(sendToDestination, loan);
      return MessageBuilder.withPayload(loan)
          .setHeader("spring.cloud.stream.sendto.destination", sendToDestination)
          .build();
    };
  }

}
