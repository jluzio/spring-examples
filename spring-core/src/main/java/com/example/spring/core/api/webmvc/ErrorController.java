package com.example.spring.core.api.webmvc;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController {

  @GetMapping(path = "/webmvc/error/users/{userId}")
  public String users(@PathVariable String userId) {
    Assert.state(StringUtils.isNumeric(userId), "userId must not be numeric");
    return "Requested details for user %s".formatted(userId);
  }

}
