package com.example.spring.data.messages.repository;

import com.example.spring.data.messages.model.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CrudRepository<Message, String> {

}
