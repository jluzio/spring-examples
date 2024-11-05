package com.example.spring.data.config;

import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class JpaConfig {

  @Bean
  @ConditionalOnMissingBean
  JpaVendorAdapter jpaVendorAdapter() {
    return new HibernateJpaVendorAdapter();
  }

  @Bean
  @ConditionalOnMissingBean
  EntityManagerFactoryBuilder entityManagerFactoryBuilder(
      JpaVendorAdapter jpaVendorAdapter,
      ObjectProvider<PersistenceUnitManager> persistenceUnitManagerProvider
  ) {
    return new EntityManagerFactoryBuilder(jpaVendorAdapter, Map.of(), persistenceUnitManagerProvider.getIfAvailable());
  }

}
