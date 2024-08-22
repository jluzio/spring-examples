package com.example.spring.data.elasticsearch.model;


import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@Document(indexName = "movie-index")
public class Movie {

  private @Id String id;
  private String title;
  private @Field(type = FieldType.Integer) String year;
  private List<String> genres;

}