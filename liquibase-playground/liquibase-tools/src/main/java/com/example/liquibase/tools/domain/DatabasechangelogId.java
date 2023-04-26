package com.example.liquibase.tools.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatabasechangelogId implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "AUTHOR")
  private String author;

  @Id
  @Column(name = "FILENAME")
  private String filename;

  @Id
  @Column(name = "ID")
  private String id;

}