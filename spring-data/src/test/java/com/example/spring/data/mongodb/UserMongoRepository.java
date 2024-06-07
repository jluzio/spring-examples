package com.example.spring.data.mongodb;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "mongodb-users", path = "mongodb-users")
public interface UserMongoRepository extends MongoRepository<User, String> {

  User findByUsername(String username);

  List<User> findByLastName(String lastName);

}
