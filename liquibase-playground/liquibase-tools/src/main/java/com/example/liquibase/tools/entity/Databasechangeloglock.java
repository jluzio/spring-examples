package com.example.liquibase.tools.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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