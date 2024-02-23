package com.example.spring.core.api.webmvc;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ObservableController {

  private final ObservationRegistry observationRegistry;


  @GetMapping(path = "/webmvc/observable/users/{userId}")
  public String users(@PathVariable String userId) {
    return Observation.createNotStarted("getUserById", observationRegistry)
        .observe(() -> {
          Assert.state(StringUtils.isNumeric(userId), "userId must not be numeric");
          return "Requested details for user %s".formatted(userId);
        });
  }

}
