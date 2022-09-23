package com.example.liquibase.tools.config;

import com.example.liquibase.tools.service.LiquibaseFactory;
import java.sql.SQLException;
import javax.sql.DataSource;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringResourceAccessor;
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
import org.springframework.core.io.ResourceLoader;

@Configuration
@EnableConfigurationProperties({LiquibaseProperties.class, LiquibaseTasksProperties.class})
@RequiredArgsConstructor
@Slf4j
public class CoreLiquibaseConfiguration {

  private final DataSource dataSource;
  private final ResourceLoader resourceLoader;


  @Bean
  public LiquibaseFactory liquibaseFactory() throws SQLException, LiquibaseException {
    Database database = DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
    ResourceAccessor resourceAccessor = resourceAccessor();
    return new LiquibaseFactory(database, resourceAccessor);
  }

  /**
   * @see liquibase.resource.ResourceAccessor
   * @see liquibase.resource.ClassLoaderResourceAccessor
   * @see liquibase.resource.CompositeResourceAccessor
   */
  private ResourceAccessor resourceAccessor() {
    return new CompositeResourceAccessor(
        new FileSystemResourceAccessor(),
        new ClassLoaderResourceAccessor(),
        new SpringResourceAccessor(resourceLoader));
  }

}