package com.example.spring.boot.playground.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@Entity
@NamedQueries({
    @NamedQuery(name = "User.namedQueryFindByEmail", query = "select u from User u where u.email = ?1")
})
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private String name;
  private String username;
  private String email;
  @Embedded
  private Address address;

}
