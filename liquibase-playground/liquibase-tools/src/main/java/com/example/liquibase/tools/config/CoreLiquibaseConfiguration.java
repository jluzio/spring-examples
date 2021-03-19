package com.example.liquibase.tools.config;

import com.example.liquibase.tools.resource.ResourceLoaderResourceAccessor;
import com.example.liquibase.tools.service.LiquibaseFactory;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({LiquibaseProperties.class, LiquibaseTasksProperties.class})
@RequiredArgsConstructor
@Slf4j
public class CoreLiquibaseConfiguration {

  private final DataSource dataSource;
  private final ResourceLoaderResourceAccessor resourceLoaderResourceAccessor;


  /**
   * @see liquibase.resource.ResourceAccessor
   * @see liquibase.resource.AbstractResourceAccessor
   * @see liquibase.resource.ClassLoaderResourceAccessor
   * @see liquibase.resource.CompositeResourceAccessor
   * @see liquibase.integration.spring.SpringLiquibase.SpringResourceOpener
   */
  @Bean
  public LiquibaseFactory liquibaseFactory() throws SQLException, LiquibaseException {
    Database database = DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));

    ResourceAccessor resourceAccessor = new CompositeResourceAccessor(
        new FileSystemResourceAccessor(),
        new ClassLoaderResourceAccessor(),
        springResourceAccessor());

    return new LiquibaseFactory(database, resourceAccessor);
  }

  private ResourceAccessor springResourceAccessor() {
//    return  new liquibase.integration.spring.SpringLiquibase()
//        .new SpringResourceOpener(properties.getChangeLog());
    return resourceLoaderResourceAccessor;
  }
}