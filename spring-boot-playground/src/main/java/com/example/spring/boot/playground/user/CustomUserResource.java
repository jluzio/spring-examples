package com.example.spring.boot.playground.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/custom/users")
public class CustomUserResource {
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
	public User findUser(@PathVariable("id") Integer id) {
		return userRepository.findById(id)
		  .orElseThrow(() -> new UserNotFoundException("id: %s".formatted(id)));
	}

	@PostMapping
	public ResponseEntity<Object> createUser(@RequestBody User user) {
		var savedUser = userRepository.save(user);
		var location = ServletUriComponentsBuilder.fromCurrentRequest()
		  .path("{id}")
		  .buildAndExpand(savedUser.getId())
		  .toUri();
		return ResponseEntity
		  .created(location)
		  .build();
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
