package com.example.spring.data.elasticsearch.model;

import static org.springframework.data.elasticsearch.annotations.FieldType.Date;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Data
@Builder
@Document(indexName = "conference-index")
public class Conference {

  private @Id String id;
  private String name;
  private @Field(type = Date) String date;
  private GeoPoint location;
  private List<String> keywords;

}