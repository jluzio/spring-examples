package com.example.spring.batch.playground.guide.repository;

import com.example.spring.batch.playground.guide.entity.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, Long> {

}
