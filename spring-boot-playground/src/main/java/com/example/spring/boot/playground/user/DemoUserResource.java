package com.example.spring.boot.playground.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/demo/users")
public class DemoUserResource {
  @Autowired
  private UserRepository userRepository;

  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public Iterable<User> listUsers() {
    return userRepository.findAll();
  }

  @GetMapping("{id}")
  public User getUser(@PathVariable("id") Integer id) {
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

  @GetMapping("search")
  public Page<User> searchUsers(UserFilter userFilter) {
    var example = Example.of(userFilter.getUser());
    var page = userFilter.getPage() != null ? userFilter.getPage().toPageable() : Pageable.unpaged();
    return userRepository.findAll(example, page);
  }

}
