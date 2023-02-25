package com.example.spring.cloud.circuitbreaker.model;

import java.time.LocalDate;
import lombok.Builder;

@Builder(toBuilder = true)
public record Todo(String id, String name, String description, LocalDate completedAt) {

}
