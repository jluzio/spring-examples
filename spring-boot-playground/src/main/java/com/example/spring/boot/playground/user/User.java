package com.example.spring.boot.playground.user;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

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
