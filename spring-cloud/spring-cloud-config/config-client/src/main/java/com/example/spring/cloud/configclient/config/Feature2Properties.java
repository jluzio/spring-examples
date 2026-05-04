package com.example.spring.cloud.configclient.config;

import com.example.spring.cloud.configclient.config.model.FeatureProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
@ConfigurationProperties("app.features.feat2")
public class Feature2Properties extends FeatureProperties {

}
