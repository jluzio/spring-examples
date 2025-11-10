package com.example.spring.framework.config.dependency.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TransferService {

  private final AccountRepository accountRepository;

}
