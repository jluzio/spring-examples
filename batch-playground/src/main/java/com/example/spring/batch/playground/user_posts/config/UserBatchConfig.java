package com.example.spring.batch.playground.user_posts.config;

import static com.example.spring.batch.playground.user_posts.config.UserBatchConfig.JOBS_ENABLED_KEY;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(JOBS_ENABLED_KEY)
@Import({SharedJobConfig.class, ImportCsvFileJobConfig.class, ImportDataJobConfig.class})
@SuppressWarnings("java:S1118")
public class UserBatchConfig {

  public static final String BASE_KEY = "app.features.user_posts";
  public static final String JOBS_ENABLED_KEY = BASE_KEY + ".jobs-enabled";
  public static final String DATA_POPULATOR_ENABLED_KEY = BASE_KEY + ".data-populator-enabled";

}
