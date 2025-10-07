package com.example.spring.scheduledtasks.persistence.repository;

import com.example.spring.scheduledtasks.persistence.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, String> {

}
