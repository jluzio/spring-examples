package org.example.todomvc.user;

import javax.persistence.Entity;

import org.example.todomvc.model.Person;

@Entity
public class User extends Person {
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
