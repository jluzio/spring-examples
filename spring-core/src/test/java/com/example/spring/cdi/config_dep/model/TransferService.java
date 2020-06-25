package com.example.spring.cdi.config_dep.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TransferService {

  private final AccountRepository accountRepository;

}
