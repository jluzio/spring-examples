package com.example.spring.core.config.dependency;

import com.example.spring.core.config.dependency.model.AccountRepository;
import com.example.spring.core.config.dependency.model.DataSource;
import com.example.spring.core.config.dependency.model.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
@Slf4j
class AutowireConfigTest {

  @Autowired
  TransferService transferService;

  @Test
  void test() {
    log.info("transferService: {}", transferService);
  }

  @Configuration
  static class ServiceConfig {

    @Autowired
    RepositoryConfig repositoryConfig;

    @Bean
    TransferService transferService() {
      return new TransferService(repositoryConfig.accountRepository());
    }
  }

  @Configuration
  static class RepositoryConfig {

    final DataSource dataSource;

    public RepositoryConfig(DataSource dataSource) {
      this.dataSource = dataSource;
    }

    @Bean
    AccountRepository accountRepository() {
      return new AccountRepository(dataSource);
    }
  }

  @Configuration
  static class SystemTestConfig {

    @Bean
    DataSource dataSource() {
      return new DataSource();
    }
  }
}
