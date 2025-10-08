package com.example.liquibase.tools.domain;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The persistent class for the DATABASECHANGELOGLOCK database table.
 */
@Entity
@Table(name = "DATABASECHANGELOGLOCK")
@NamedQuery(name = "Databasechangeloglock.findAll", query = "SELECT d FROM Databasechangeloglock d")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Databasechangeloglock implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "ID")
  private int id;

  @Column(name = "LOCKED")
  private byte locked;

  @Column(name = "LOCKEDBY")
  private String lockedby;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "LOCKGRANTED")
  private Date lockgranted;

}