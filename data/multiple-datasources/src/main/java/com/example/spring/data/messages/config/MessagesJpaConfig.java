package com.example.spring.data.messages.config;

import com.example.spring.data.messages.model.Message;
import com.example.spring.data.messages.repository.MessageRepository;
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
    basePackageClasses = MessageRepository.class,
    entityManagerFactoryRef = "messagesEntityManagerFactory",
    transactionManagerRef = "messagesTransactionManager"
)
public class MessagesJpaConfig {

  @Bean
  @ConfigurationProperties("messages.datasource")
  public DataSourceProperties messagesDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource messagesDataSource() {
    return messagesDataSourceProperties()
        .initializeDataSourceBuilder()
        .build();
  }

  @Bean
  @ConfigurationProperties("messages.jpa.properties")
  public Map<String, String> messagesJpaProperties() {
    return new HashMap<>();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean messagesEntityManagerFactory(
      @Qualifier("messagesDataSource") DataSource dataSource,
      @Qualifier("messagesJpaProperties") Map<String, ?> jpaProperties,
      EntityManagerFactoryBuilder builder
  ) {
    return builder
        .dataSource(dataSource)
        .packages(Message.class)
        .properties(jpaProperties)
        .build();
  }

  @Bean
  public PlatformTransactionManager messagesTransactionManager(
      @Qualifier("messagesEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory
  ) {
    return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
  }

}