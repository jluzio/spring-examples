package com.example.spring.cdi.config_dep.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AccountRepository {
  private final DataSource dataSource;

}
