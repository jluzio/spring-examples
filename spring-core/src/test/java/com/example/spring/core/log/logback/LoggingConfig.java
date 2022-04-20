package com.example.spring.core.log.logback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ConverterConfig.class)
public class LoggingConfig {

  @Autowired
  void initMaskConverter(MaskConverter maskConverter) {
    MaskConverterProxy.setConverter(maskConverter);
  }

}
