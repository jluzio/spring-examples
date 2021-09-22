package com.example.spring.batch.playground.guide.job;


import com.example.spring.batch.playground.guide.entity.Post;
import com.example.spring.batch.playground.guide.entity.User;
import com.example.spring.batch.playground.guide.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostItemProcessor implements ItemProcessor<Post, Post> {

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
