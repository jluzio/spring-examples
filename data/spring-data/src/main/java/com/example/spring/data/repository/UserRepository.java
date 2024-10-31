package com.example.spring.data.repository;

import com.example.spring.data.jpa.model.User;
import com.example.spring.data.jpa.model.UserStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  List<User> findByName(@Param("name") String name);

  @Modifying
  @Query("update User u set u.status = ?1 where u.name = ?2")
  void updateStatusByName(UserStatus status, String name);

}