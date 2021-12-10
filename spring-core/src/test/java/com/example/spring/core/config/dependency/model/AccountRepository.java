package com.example.spring.core.config.dependency.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AccountRepository {

  private final DataSource dataSource;

}
