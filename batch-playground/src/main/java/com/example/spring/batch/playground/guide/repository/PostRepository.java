package com.example.spring.batch.playground.guide.repository;

import com.example.spring.batch.playground.guide.entity.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {

}
