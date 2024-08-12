package com.example.spring.data.repository;

import static com.example.spring.data.jpa.model.QUser.user;

import com.example.spring.data.jpa.model.User;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
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
    return queryFactory.selectFrom(user)
        .where(user.name.eq(name))
        .orderBy(user.createdAt.asc())
        .fetch();
  }

  @Override
  public List<Tuple> findIdAndEmail() {
    var queryFactory = new JPAQueryFactory(entityManager);
    return queryFactory.select(user.id, user.email)
        .from(user)
        .orderBy(user.createdAt.asc())
        .fetch();
  }

  @Override
  public List<User> findUsersWithLongestEmail() {
    var queryFactory = new JPAQueryFactory(entityManager);
    var otherUser = user;
    return queryFactory.selectFrom(user)
        .where(user.email.length().eq(
            JPAExpressions.select(otherUser.email.length().max())
                .from(otherUser))
        )
        .fetch();
  }
}
