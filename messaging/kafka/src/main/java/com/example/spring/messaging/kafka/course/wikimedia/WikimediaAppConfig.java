package com.example.spring.messaging.kafka.course.wikimedia;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("wikimedia")
@Import({WikimediaChangesProducer.class, WikimediaConfig.class})
public class WikimediaAppConfig {

}
