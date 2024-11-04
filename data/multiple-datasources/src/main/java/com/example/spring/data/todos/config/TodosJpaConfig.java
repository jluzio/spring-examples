package com.example.spring.data.todos.config;

import com.example.spring.data.todos.model.Todo;
import com.example.spring.data.todos.repository.TodoRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackageClasses = TodoRepository.class,
    entityManagerFactoryRef = "todosEntityManagerFactory",
    transactionManagerRef = "todosTransactionManager"
)
public class TodosJpaConfig {


  @Bean
  @ConfigurationProperties("todos.datasource")
  public DataSourceProperties todosDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource todosDataSource() {
    return todosDataSourceProperties()
        .initializeDataSourceBuilder()
        .build();
  }

  @Bean
  @ConfigurationProperties("todos.jpa")
  public Map<String, String> todosJpaProperties() {
    return new HashMap<>();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean todosEntityManagerFactory(
      @Qualifier("todosDataSource") DataSource dataSource,
      @Qualifier("todosJpaProperties") Map<String, ?> jpaProperties,
      EntityManagerFactoryBuilder builder
  ) {
    return builder
        .dataSource(dataSource)
        .packages(Todo.class)
        .properties(jpaProperties)
        .build();
  }

  @Bean
  public PlatformTransactionManager todosTransactionManager(
      @Qualifier("todosEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory
  ) {
    return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
  }

}