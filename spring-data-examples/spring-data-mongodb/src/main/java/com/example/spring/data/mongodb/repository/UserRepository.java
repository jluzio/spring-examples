package com.example.spring.data.mongodb.repository;

import com.example.spring.data.mongodb.model.User;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends MongoRepository<User, String> {

  User findByUsername(String username);

  List<User> findByName(String name);

}
