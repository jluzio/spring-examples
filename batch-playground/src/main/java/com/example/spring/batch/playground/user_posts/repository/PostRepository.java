package com.example.spring.batch.playground.user_posts.repository;

import com.example.spring.batch.playground.user_posts.entity.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {

}
