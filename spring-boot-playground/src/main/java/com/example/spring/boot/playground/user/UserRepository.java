package com.example.spring.boot.playground.user;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RestResource(path = "users")
public interface UserRepository extends CrudRepository<User, Integer> {
	
	Optional<User> findFirstByName(String name);
	
}
