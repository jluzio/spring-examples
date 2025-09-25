package com.example.spring.batch.playground;

import com.example.spring.batch.playground.features.user.persistence.model.User;
import com.example.spring.batch.playground.features.user.persistence.repository.UserRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Meant to be used with {@link org.springframework.batch.test.context.SpringBatchTest}
 */
@TestConfiguration
@EnableAutoConfiguration
@EntityScan(basePackageClasses = User.class)
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class BaseSpringBatchTestConfig {

}
