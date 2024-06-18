package com.example.spring.data.redis.model;

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
