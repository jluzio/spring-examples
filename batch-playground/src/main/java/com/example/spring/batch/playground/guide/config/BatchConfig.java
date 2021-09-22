package com.example.spring.batch.playground.guide.config;

import com.example.spring.batch.playground.guide.entity.Person;
import com.example.spring.batch.playground.guide.listener.JobCompletionNotificationListener;
import com.example.spring.batch.playground.guide.processor.PersonItemProcessor;
import com.example.spring.batch.playground.guide.repository.PersonRepository;
import java.lang.reflect.Method;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

  @Autowired
  private JobBuilderFactory jobBuilderFactory;

  @Autowired
  private StepBuilderFactory stepBuilderFactory;

  @Bean
  public FlatFileItemReader<Person> personItemReader() {
    return new FlatFileItemReaderBuilder<Person>()
        .name("personItemReader")
        .resource(new ClassPathResource("sample-data.csv"))
        .delimited()
        .names("firstName", "lastName")
        .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
          setTargetType(Person.class);
        }})
        .build();
  }

  @Bean
  public PersonItemProcessor personItemProcessor() {
    return new PersonItemProcessor();
  }

  @Bean
  public RepositoryItemWriter<Person> personItemWriter(PersonRepository personRepository) {
    return new RepositoryItemWriterBuilder<Person>()
        .repository(personRepository)
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

  @Bean
  public Job importPersonJob(JobCompletionNotificationListener listener, Step step1) {
    return jobBuilderFactory.get("importPersonJob")
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .flow(step1)
        .end()
        .build();
  }

  @Bean
  public Step step1(
      ItemReader<Person> reader,
      ItemProcessor<Person, Person> processor,
      ItemWriter<Person> writer) {
    return stepBuilderFactory.get("step1")
        .<Person, Person>chunk(10)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }
}
