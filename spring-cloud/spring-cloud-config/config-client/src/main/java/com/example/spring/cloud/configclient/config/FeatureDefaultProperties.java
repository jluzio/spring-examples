package com.example.spring.cloud.configclient.config;

import com.example.spring.cloud.configclient.config.model.FeatureProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app.features.featdef")
public class FeatureDefaultProperties extends FeatureProperties {

}
