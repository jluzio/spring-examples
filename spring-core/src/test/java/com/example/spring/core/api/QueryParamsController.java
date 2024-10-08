package com.example.spring.core.api;

import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
class QueryParamsController {

  @GetMapping("/query-params")
  public ParamsData params(ParamsData data) {
    log.debug(data);
    return data;
  }

  @Data
  @Builder
  static class ParamsData {

    private String paramFoo;
    private String paramBar;

    public void setParam_bar(String paramBar) {
      setParamBar(paramBar);
    }
  }

}
