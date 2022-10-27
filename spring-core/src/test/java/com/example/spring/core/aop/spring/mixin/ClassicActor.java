package com.example.spring.core.aop.spring.mixin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassicActor implements ClassicPerformer {

  @Override
  public void act() {
    log.info("Does some acting!");
  }
}
