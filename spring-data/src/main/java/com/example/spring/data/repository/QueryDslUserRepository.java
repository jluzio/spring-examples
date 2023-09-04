package com.example.spring.data.repository;

import com.example.spring.data.jpa.model.User;
import com.querydsl.core.Tuple;
import java.util.List;

public interface QueryDslUserRepository {

  List<User> findByNameOrderByCreatedAt(String name);

  List<Tuple> findIdAndEmail();

  List<User> findUsersWithLongestEmail();
}
