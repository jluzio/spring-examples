package com.example.spring.boot.playground.api;

import com.example.spring.boot.playground.exception.NotFoundException;
import com.example.spring.boot.playground.model.User;
import com.example.spring.boot.playground.model.UserFilter;
import com.example.spring.boot.playground.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        .orElseThrow(() -> new NotFoundException("id: %s".formatted(id)));
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

  @PatchMapping("{id}")
  public User patchUser(@PathVariable("id") Integer id, @RequestBody User user) {
    throw new UnsupportedOperationException("TODO");
  }

  @DeleteMapping("{id}")
  public void deleteUser(@PathVariable("id") Integer id) {
    userRepository.deleteById(id);
  }

  @GetMapping("search")
  public Page<User> searchUsers(UserFilter userFilter) {
    var example = Example.of(userFilter.getUser());
    var page =
        userFilter.getPage() != null ? userFilter.getPage().toPageable() : Pageable.unpaged();
    return userRepository.findAll(example, page);
  }

}
