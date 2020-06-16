package com.example.spring.boot.playground.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.spring.boot.playground.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
	
	Optional<User> findFirstByName(String name);
	
}
