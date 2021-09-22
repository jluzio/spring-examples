package com.example.spring.batch.playground.guide.job;


import com.example.spring.batch.playground.guide.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserItemProcessor implements ItemProcessor<User, User> {

  @Override
  public User process(User user) throws Exception {
    var transformedUser = new User(
        user.getId(),
        user.getName().toUpperCase(),
        user.getUsername(),
        user.getEmail());
    log.info("Converting ({}) into ({})", user, transformedUser);
    return transformedUser;
  }
}
