package com.example.spring.data.jpa.model;

import com.example.spring.data.jpa.model.converter.UserStatusConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.Data;

@Entity
@Table(name = "app_user")
@Data
public class User {

  @Id
  private String id;
  private String name;
  private String email;
  @ManyToOne(optional = false)
  @JoinColumn(name = "fk_role_id", referencedColumnName = "ID")
  private Role role;
  @Convert(converter = UserStatusConverter.class)
  private UserStatus status;
  private OffsetDateTime createdAt;

  @PrePersist
  @PreUpdate
  public void onUpdate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
  }
}