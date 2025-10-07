package com.example.spring.batch.playground.features.user.persistence.repository;

import com.example.spring.batch.playground.features.user.persistence.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {

}
