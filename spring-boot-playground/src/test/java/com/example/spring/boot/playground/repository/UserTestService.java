package com.example.spring.boot.playground.repository;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserTestService {

  @Autowired
  UserTestRepository repository;

  int setFixedNameFor(String name, Collection<String> usernames) {
    return repository.setFixedNameFor(name, usernames);
  }

}