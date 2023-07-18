package com.example.spring.data.repository;

import com.example.spring.data.jpa.model.QUser;
import com.example.spring.data.jpa.model.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class QueryDslUserRepositoryImpl implements QueryDslUserRepository {

  @PersistenceContext
  EntityManager entityManager;

  @Override
  public List<User> findByNameOrderByCreatedAt(String name) {
    var queryFactory = new JPAQueryFactory(entityManager);
    QUser user = QUser.user;
    return queryFactory.selectFrom(user)
        .where(user.name.eq(name))
        .orderBy(user.createdAt.asc())
        .fetch();
  }
}
