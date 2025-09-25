package com.example.spring.batch.playground.features.user.persistence.repository;

import com.example.spring.batch.playground.features.user.persistence.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, String> {

}
