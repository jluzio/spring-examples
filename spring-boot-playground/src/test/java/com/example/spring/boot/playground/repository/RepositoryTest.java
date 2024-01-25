package com.example.spring.boot.playground.repository;

import com.example.spring.boot.playground.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@Slf4j
public class RepositoryTest {

  @Autowired
  UserTestRepository repository;
  @Autowired
  TransactionTemplate transactionTemplate;
  @Autowired
  UserTestService service;

  @Test
  @Transactional
  void test() {
    saveUsers();

    var namedQueryFind = repository.namedQueryFindByEmail("name-1@mail.org");
    log.info("namedQueryFind: {}", namedQueryFind);

    var findByNameAndMail = repository.findByNameIgnoreCaseAndEmailIgnoreCase("NAMe-1",
        "nAmE-1@mail.org");
    log.info("findByNameAndMail: {}", findByNameAndMail);

    var findByNameOrdered = repository.findByNameContainingOrderByIdDesc("ame-");
    log.info("findByNameOrdered: {}", findByNameOrdered);

    var customFindByNameOrdered = repository.customFindByNameContainingOrderByIdDesc("ame-");
    log.info("customFindByNameOrdered: {}", customFindByNameOrdered);

    var nameSort = repository.findByNameContaining("name", Sort.by(Order.desc("id")));
    log.info("nameSort: {}", nameSort);

    var nameSlice = repository.findByNameContaining("name",
        PageRequest.of(1, 2, Direction.DESC, "id"));
    log.info("nameSlice: {} | {}", nameSlice, nameSlice.getContent());

    var customParamName = repository.customFindByNameUsingParamName("name-1");
    log.info("customParamName: {}", customParamName);

    var customSpel = repository.customFindByNameSpEL("name-1");
    log.info("customSpel: {}", customSpel);

    var nativeResult = repository.findByEmailAddressContainingNative("mail.org",
        PageRequest.of(1, 2, Direction.DESC, "id"));
    log.info("nativeResult: {} | {} | {}",
        nativeResult,
        nativeResult.getContent(),
        nativeResult.getTotalPages());

    transactionTemplate.execute(status -> {
      int updated = repository.setFixedNameFor("fixed-name", Set.of("username-1", "username-3"));
      log.info("updated: {}", updated);
      log.info("updated.find: {}", repository.findByName("fixed-name"));
      return updated;
    });

    int updated2 = service.setFixedNameFor("fixed-name-2", Set.of("username-4"));
    log.info("updated2: {}", updated2);
    log.info("updated2.find: {}", repository.findByName("fixed-name-2"));

    repository.findAll(Pageable.unpaged())
        .map(User::getId)
        .forEach(id -> log.info("findAll :: id={}", id));

    repository.readAllByNameNotNull()
        .map(User::getId)
        .forEach(id -> log.info("readAll :: id={}", id));

    var asyncUser = repository.findOneByUsername("username-4");
    log.info("async user start");
    asyncUser.handleAsync((user, error) -> {
      log.info("async user: {}", user);
      return user;
    });

  }


  private void saveUsers() {
    var users = IntStream.rangeClosed(1, 10)
        .mapToObj(i -> {
          var user = new User();
          user.setName("name-%s".formatted(i));
          user.setUsername("username-%s".formatted(i));
          user.setEmail("name-%s@mail.org".formatted(i));
          return user;
        })
        .collect(Collectors.toList());
    repository.saveAll(users);
  }

  private Stream<User> userFindAllStream() {
    return StreamSupport.stream(repository.findAll().spliterator(), false);
  }


}
