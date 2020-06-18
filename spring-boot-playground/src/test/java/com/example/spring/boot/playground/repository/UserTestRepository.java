package com.example.spring.boot.playground.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.concurrent.ListenableFuture;

import com.example.spring.boot.playground.user.User;

@Repository
public interface UserTestRepository extends CrudRepository<User, Integer>, PagingAndSortingRepository<User, Integer> {

  List<User> findByName(String name);

  /** Using named query in User class **/
  Optional<User> namedQueryFindByEmail(String email);

  Optional<User> findByNameIgnoreCaseAndEmailIgnoreCase(String name, String email);

  List<User> findByNameContainingOrderByIdDesc(String name);

  @Query("select u from User u where u.name like %?1% order by id desc")
  List<User> customFindByNameContainingOrderByIdDesc(String name);

  List<User> findByNameContaining(String name, Sort sort);

  Page<User> findByNameContaining(String name, Pageable page);

  @Query("select u from User u where u.name = :name")
  List<User> customFindByNameUsingParamName(String name);

  @Query("select u from #{#entityName} u where u.name = ?1")
  List<User> customFindByNameSpEL(String name);

  @Query(value = "SELECT * FROM USER WHERE EMAIL like %?1%", countName = "SELECT count(*) FROM USER WHERE EMAIL like %?1%", nativeQuery = true)
  Page<User> findByEmailAddressContaningNative(String emailAddress, Pageable pageable);

  @Modifying
  @Query("update User u set u.name = ?1 where u.username in ?2")
  int setFixedNameFor(String name, Collection<String> usernames);

  Stream<User> readAllByNameNotNull();

  @Async
  Future<User> findByUsername(String username);

  @Async
  CompletableFuture<User> findOneByUsername(String username);

  @Async
  ListenableFuture<User> findOneByEmail(String email);

}
