package com.example.tools.flyway.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Arrays;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@UtilityClass
public class DataPopulators {

  public static Jackson2RepositoryPopulatorFactoryBean dataPopulator(String... resources) {
    Jackson2RepositoryPopulatorFactoryBean factoryBean = new Jackson2RepositoryPopulatorFactoryBean();
    factoryBean.setMapper(new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    );
    factoryBean.setResources(
        Arrays.stream(resources)
            .map(ClassPathResource::new)
            .toArray(Resource[]::new));
    return factoryBean;
  }

}
