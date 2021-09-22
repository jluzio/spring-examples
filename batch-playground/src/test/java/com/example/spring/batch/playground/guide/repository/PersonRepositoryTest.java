package com.example.spring.batch.playground.guide.repository;

import com.example.spring.batch.playground.guide.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
@Slf4j
class PersonRepositoryTest {

  @Autowired
  private PersonRepository personRepository;

  @Test
  void repository() {
    log.info("--- initial data ---");
    Flux.fromIterable(personRepository.findAll())
        .doOnNext(person -> log.info("person: {}", person))
        .blockLast();

    log.info("--- saving new person ---");
    Person newPerson = Person.builder()
        .firstName("Tester")
        .lastName("Doe")
        .build();
    personRepository.save(newPerson);

    log.info("--- final data ---");
    Flux.fromIterable(personRepository.findAll())
        .doOnNext(person -> log.info("person: {}", person))
        .blockLast();

    log.info("done");
  }

}
