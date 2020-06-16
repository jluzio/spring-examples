package com.example.spring.boot.playground.api.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring.boot.playground.model.User;
import com.example.spring.boot.playground.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserRepository userRepository;

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public Iterable<User> getUsers() {
		return userRepository.findAll();
	}

	@GetMapping("name/{name}")
	public Optional<User> findFirstByName(@PathVariable("name") String name) {
		return userRepository.findFirstByName(name);
	}

	@GetMapping("{id}")
	public Optional<User> findUser(@PathVariable("id") Integer id) {
		return userRepository.findById(id);
	}

	@PostMapping
	public User createUser(@RequestBody User user) {
		return userRepository.save(user);
	}

	@PutMapping("{id}")
	public User updateUser(@PathVariable("id") Integer id, @RequestBody User user) {
		return userRepository.save(user);
	}

	@DeleteMapping("{id}")
	public void deleteUser(@PathVariable("id") Integer id) {
		userRepository.deleteById(id);
	}

}
