package com.example.spring.batch.playground.user_posts.batch;


import com.example.spring.batch.playground.user_posts.persistence.model.Post;
import com.example.spring.batch.playground.user_posts.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostFilterByUserItemProcessor implements ItemProcessor<Post, Post> {

  private final UserRepository userRepository;

  @Override
  public Post process(Post post) throws Exception {
    if (userRepository.existsById(post.getUserId())) {
      return post;
    } else {
      return null;
    }
  }
}
