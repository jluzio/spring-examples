package com.example.spring.boot.playground.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.spring.boot.playground.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

}
