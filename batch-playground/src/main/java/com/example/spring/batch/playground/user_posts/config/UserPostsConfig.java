package com.example.spring.batch.playground.user_posts.config;

import com.example.spring.batch.playground.annotation.ConditionalOnAutoConfigureApp;
import com.example.spring.batch.playground.user_posts.config.batch.BatchConfig;
import com.example.spring.batch.playground.user_posts.config.batch.ItemIOConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnAutoConfigureApp
@Import({DataConfig.class, BatchConfig.class, ItemIOConfig.class})
public class UserPostsConfig {

}
