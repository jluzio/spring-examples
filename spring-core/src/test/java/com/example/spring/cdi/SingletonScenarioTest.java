package com.example.spring.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
@Slf4j
public class SingletonScenarioTest {

  @Resource
  ClientService clientService1;
  @Resource
  ClientService clientService2;

  @Test
  void test() {
    // due to configurations being proxied, the instances are the same
    assertEquals(clientService1.clientDao, clientService2.clientDao);
  }

  @Configuration
  static class Config {

    @Bean
    public ClientService clientService1() {
      ClientService clientService = new ClientService();
      clientService.setClientDao(clientDao());
      return clientService;
    }

    @Bean
    public ClientService clientService2() {
      ClientService clientService = new ClientService();
      clientService.setClientDao(clientDao());
      return clientService;
    }

    @Bean
    public ClientDao clientDao() {
      return new ClientDao();
    }
  }

  @Data
  static class ClientService {

    private ClientDao clientDao;
  }

  @Data
  static class ClientDao {

  }

}
