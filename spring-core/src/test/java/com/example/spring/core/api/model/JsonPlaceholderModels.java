package com.example.spring.core.api.model;

public class JsonPlaceholderModels {

  public record Todo(int userId, int id, String title, boolean completed) {

  }

}
