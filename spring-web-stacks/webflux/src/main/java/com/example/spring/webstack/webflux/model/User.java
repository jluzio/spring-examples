package com.example.spring.webstack.webflux.model;

import lombok.Builder;

@Builder
public record User(String id, String username, String fullName, String email) {

}
