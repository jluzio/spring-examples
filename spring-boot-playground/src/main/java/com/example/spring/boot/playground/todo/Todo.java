package com.example.spring.boot.playground.todo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

import com.example.spring.boot.playground.user.User;

import lombok.Data;

@Data
@XmlRootElement
@Entity
public class Todo {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private String title;
  private boolean completed;
  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;

}
