package com.example.spring.core.aop.spring.mixin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImprovActor implements ImprovisationPerformer {

  public ImprovActor() {
    log.info("ImprovActor<>");
  }

  @Override
  public void improvise() {
    log.info("Does some improvising!");
  }
}
