package com.example.spring.data.api.test;

import com.example.spring.data.jpa.model.User;
import com.example.spring.data.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tx")
@RequiredArgsConstructor
@Log4j2
public class TestTransactionController {

  private final UserRepository userRepository;
  private final TestTransactionService service;

  /**
   * @see org.springframework.transaction.interceptor.TransactionInterceptor
   * @see org.springframework.transaction.PlatformTransactionManager
   * @see org.springframework.transaction.support.AbstractPlatformTransactionManager
   * @see org.springframework.data.jpa.repository.support.SimpleJpaRepository
   */
  @GetMapping("/users/{id}")
  @Transactional(propagation = Propagation.NEVER)
  public Optional<User> usersByRepository(@PathVariable String id) {
    log.debug("begin");

    // method is not intercepted, due to no @Transactional
    service.execute();

    // method is intercepted, due to @Transactional
    service.getUserById(id);

    // calls from repository are intercepted due to @Transactional (see SimpleJpaRepository)
    return userRepository.findById(id);
  }

}
