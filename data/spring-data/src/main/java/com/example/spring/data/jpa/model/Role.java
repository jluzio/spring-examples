package com.example.spring.data.jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = "value")
)
@NoArgsConstructor
public class Role {

  @Id
  private String id;
  private String value;

  public Role(String id) {
    this.id = id;
  }
}