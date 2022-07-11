package com.example.spring.batch.playground.user_posts.repository;

import com.example.spring.batch.playground.user_posts.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}
