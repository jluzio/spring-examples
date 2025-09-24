package com.example.spring.batch.playground.user_posts.batch;


import com.example.spring.batch.playground.persistence.IdGenerators;
import com.example.spring.batch.playground.user_posts.persistence.model.User;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class UserEnsureIdItemProcessor implements ItemProcessor<User, User> {

  @Override
  public User process(User user) throws Exception {
    var transformedUser = user.toBuilder()
        .id(Optional.ofNullable(user.getId())
            .filter(StringUtils::hasText)
            .orElseGet(IdGenerators::uuid))
        .build();
    log.info("Converting ({}) into ({})", user, transformedUser);
    return transformedUser;
  }
}
