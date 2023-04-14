package com.example.spring.data.repository;

import com.example.spring.data.jpa.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {

  List<User> findByName(@Param("name") String name);

}