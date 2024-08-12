package com.example.spring.data.mongodb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.example.spring.data.mongodb.model.Product;
import com.example.spring.data.mongodb.repository.UserRepository;
import com.mongodb.client.MongoClient;
import lombok.extern.log4j.Log4j2;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("docker-compose-test")
@Log4j2
class DockerComposeTest {

  @Autowired
  private UserRepository repository;
  @Autowired
  MongoClient mongoClient;

  @Test
  void repository_check_populated() {
    var users = repository.findAll();
    log.debug("users: {}", users);
    assertThat(users)
        .hasSizeGreaterThan(2);
  }

  @Test
  void mongoTemplate() {
    var mongoOps = new MongoTemplate(mongoClient, "test");
    boolean dropCollectionAtEnd = false;

    var product = new Product("T-shirt", 10.99, "Cool T-shirt");
    mongoOps.insert(product);

    Product queryResult = mongoOps.query(Product.class)
        .matching(
            where("name").is("T-shirt")
                .and("price").gt(10)
        )
        .firstValue();
    log.debug(queryResult);

    mongoOps.execute(Product.class, collection -> {
      Bson query = BsonDocument.parse("""
          {price: {$gt: 10}}
          """);
      var results = collection
          .find(query)
          .limit(1);
      for (Document doc : results) {
        log.debug("result: {}", doc);
      }

      return null;
    });

    if (dropCollectionAtEnd) {
      mongoOps.dropCollection(Product.class);
    }
  }
}
