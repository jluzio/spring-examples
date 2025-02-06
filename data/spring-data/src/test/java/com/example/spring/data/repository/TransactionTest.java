package com.example.spring.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.jpa.config.DataPopulatorConfig;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;

@DataJpaTest
@Import(DataPopulatorConfig.class)
@Slf4j
class TransactionTest {

  @Autowired
  UserRepository userRepository;
  @MockitoSpyBean
  PlatformTransactionManager transactionManager;

  @Test
  void findAll() {
    var values = Lists.newArrayList(userRepository.findAll());
    log.debug("values: {}", values);
    assertThat(values)
        .isNotEmpty();
  }

}