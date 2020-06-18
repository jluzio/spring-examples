package com.example.spring.boot.playground.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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

}
