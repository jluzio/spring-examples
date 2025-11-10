package com.example.spring.modulith.pet;

import lombok.Builder;

@Builder(toBuilder = true)
public record Pet(String id, String name, String description, String owner) {

}
