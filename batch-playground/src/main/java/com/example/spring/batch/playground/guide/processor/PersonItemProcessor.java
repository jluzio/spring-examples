package com.example.spring.batch.playground.guide.processor;


import com.example.spring.batch.playground.guide.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

  @Override
  public Person process(Person person) throws Exception {
    var transformedPerson = new Person(
        person.getId(),
        person.getFirstName().toUpperCase(),
        person.getLastName().toUpperCase());
    log.info("Converting ({}) into ({})", person, transformedPerson);
    return transformedPerson;
  }
}
