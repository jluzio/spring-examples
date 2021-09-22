package com.example.spring.batch.playground.guide.repository;

import com.example.spring.batch.playground.guide.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}
