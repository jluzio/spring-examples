package com.example.tools.flyway.persistence.repository;

import com.example.tools.flyway.persistence.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {

}
