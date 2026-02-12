package com.example.tools.flyway.config;

import java.util.Arrays;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.JacksonRepositoryPopulatorFactoryBean;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

@UtilityClass
public class DataPopulators {

  public static JacksonRepositoryPopulatorFactoryBean dataPopulator(String... resources) {
    var factoryBean = new JacksonRepositoryPopulatorFactoryBean();
    factoryBean.setMapper(JsonMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build()
    );
    factoryBean.setResources(
        Arrays.stream(resources)
            .map(ClassPathResource::new)
            .toArray(Resource[]::new));
    return factoryBean;
  }

}
