package com.example.spring.data.redis.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class SampleDataPojo {

  String id;
  String value;
}
