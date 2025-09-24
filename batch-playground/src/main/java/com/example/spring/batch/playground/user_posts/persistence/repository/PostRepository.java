package com.example.spring.batch.playground.user_posts.persistence.repository;

import com.example.spring.batch.playground.user_posts.persistence.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, String> {

}
