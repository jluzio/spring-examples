package com.example.spring.data.api.test;

import com.example.spring.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class TestTransactionService {

  private final UserRepository userRepository;

  public void execute() {
    log.debug("{}.execute()", getClass().getSimpleName());
  }

  @Transactional
  public void getUserById(Long id) {
    log.debug("{}.getUserById()", getClass().getSimpleName());
    log.debug(userRepository.findById(id));
  }

}
