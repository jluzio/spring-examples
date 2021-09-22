package com.example.spring.batch.playground.guide.job;

import com.example.spring.batch.playground.guide.entity.Post;
import com.example.spring.batch.playground.guide.entity.User;
import com.example.spring.batch.playground.guide.repository.PostRepository;
import com.example.spring.batch.playground.guide.repository.UserRepository;
import java.net.MalformedURLException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;

@Configuration
public class ItemConfig {

  @Bean
  public ItemReader<User> userItemReader() {
    return new FlatFileItemReaderBuilder<User>()
        .name("userItemReader")
        .resource(new ClassPathResource("sample-data.csv"))
        .delimited()
        .names("name", "username", "email")
        .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
          setTargetType(User.class);
        }})
        .build();
  }

  @Bean
  public ItemReader<Post> postItemReader() throws MalformedURLException {
    return new JsonItemReaderBuilder<Post>()
        .name("postItemReader")
        .resource(new UrlResource("https://jsonplaceholder.typicode.com/posts"))
        .jsonObjectReader(new JacksonJsonObjectReader<>(Post.class))
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
  public ItemWriter<Post> postRepositoryItemWriter(PostRepository repository) {
    return new RepositoryItemWriterBuilder<Post>()
        .repository(repository)
        .methodName("save")
        .build();
  }

/*
  @Bean
  public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Person>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
        .dataSource(dataSource)
        .build();
  }
*/

}
