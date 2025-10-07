package com.example.spring.scheduledtasks.persistence.repository;

import com.example.spring.scheduledtasks.persistence.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {

}
