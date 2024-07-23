package com.example.spring.messaging.kafka.fizzbuzz;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("fizz-buzz")
@Import(TestChannelBinderConfiguration.class)
@Slf4j
class FizzBuzzStreamTest {

  @Autowired
  private InputDestination input;
  @Autowired
  private OutputDestination output;

  private final long timeout = TimeUnit.SECONDS.toMillis(1);
  private final String inputDestinationName = "numbers";
  private final String outputBindingName = "fizz-buzz";


  @Test
  void test_with_defined_input_output_names() {
    input.send(new GenericMessage<>("13".getBytes()), inputDestinationName);
    assertThat(output.receive(timeout, outputBindingName).getPayload())
        .isEqualTo("13".getBytes());

    input.send(new GenericMessage<>("15".getBytes()), inputDestinationName);
    assertThat(output.receive(timeout, outputBindingName).getPayload())
        .isEqualTo("FizzBuzz".getBytes());
  }

  @Test
  void test_with_message_builder_and_default_input_type() {
    input.send(MessageBuilder.withPayload(13).build(), inputDestinationName);
    assertThat(output.receive(timeout, outputBindingName).getPayload())
        .isEqualTo("13".getBytes());

    input.send(MessageBuilder.withPayload(15).build(), inputDestinationName);
    assertThat(output.receive(timeout, outputBindingName).getPayload())
        .isEqualTo("FizzBuzz".getBytes());
  }

}
