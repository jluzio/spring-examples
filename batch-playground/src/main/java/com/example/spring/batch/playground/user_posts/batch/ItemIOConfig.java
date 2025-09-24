package com.example.spring.batch.playground.user_posts.batch;

import com.example.spring.batch.playground.user_posts.persistence.model.Post;
import com.example.spring.batch.playground.user_posts.persistence.model.User;
import com.example.spring.batch.playground.user_posts.persistence.repository.PostRepository;
import com.example.spring.batch.playground.user_posts.persistence.repository.UserRepository;
import java.net.MalformedURLException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;

@Configuration
public class ItemIOConfig {

  @Bean
  public ItemReader<User> userSampleDataCsvItemReader() {
    var mapper = new BeanWrapperFieldSetMapper<User>();
    mapper.setTargetType(User.class);

    return new FlatFileItemReaderBuilder<User>()
        .name("userItemReader")
        .resource(new ClassPathResource("sample-data.csv"))
        .delimited()
        .names("id", "name", "username", "email")
        .fieldSetMapper(mapper)
        .build();
  }

  @Bean
  public ItemWriter<User> userRepositoryItemWriter(UserRepository repository) {
    return new RepositoryItemWriterBuilder<User>()
        .repository(repository)
        .methodName("save")
        .build();
  }

  @Bean
  public ItemReader<Post> postExternalApiItemReader() throws MalformedURLException {
    return new JsonItemReaderBuilder<Post>()
        .name("postItemReader")
        .resource(new UrlResource("https://jsonplaceholder.typicode.com/posts"))
        .jsonObjectReader(new JacksonJsonObjectReader<>(Post.class))
        .build();
  }

  @Bean
  public ItemWriter<Post> postRepositoryItemWriter(PostRepository repository) {
    return new RepositoryItemWriterBuilder<Post>()
        .repository(repository)
        .methodName("save")
        .build();
  }

}
