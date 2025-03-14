package com.example.spring.ldap.repository;

import com.example.spring.ldap.model.Person;
import java.util.List;
import javax.naming.InvalidNameException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class PersonRepositoryTest {

  @Autowired
  PersonRepository repository;

  @Test
  void test() {
    List<Person> persons = repository.findAll();
    log.debug("persons: {}", persons);
  }

  @Test
  void save() throws InvalidNameException {
    var person = Person.builder()
//        .dn(new LdapName("cn=jdoe,ou=test_person_data,dc=example,dc=org"))
        .fullName("John Doe")
        .company("DoeCorp")
        .description("It's John Doe")
        .surname("Doe")
        .build();
    person = repository.save(person);
    log.debug("{}", person);

    var persons = repository.findAll();
    log.debug("persons: {}", persons);
  }

}