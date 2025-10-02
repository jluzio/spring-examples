package com.example.spring.batch.playground.features.resilience.persistence.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class JobData {

  @Id
  private String id;
  private String data;

}
