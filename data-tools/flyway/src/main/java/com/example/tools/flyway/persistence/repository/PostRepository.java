package com.example.tools.flyway.persistence.repository;

import com.example.tools.flyway.persistence.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, String> {

}
