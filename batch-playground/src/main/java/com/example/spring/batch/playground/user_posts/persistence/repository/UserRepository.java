package com.example.spring.batch.playground.user_posts.persistence.repository;

import com.example.spring.batch.playground.user_posts.persistence.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {

}
