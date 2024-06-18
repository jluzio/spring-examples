package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.model.User;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends ListCrudRepository<User, String> {

  List<User> findByName(@Param("name") String name);

}