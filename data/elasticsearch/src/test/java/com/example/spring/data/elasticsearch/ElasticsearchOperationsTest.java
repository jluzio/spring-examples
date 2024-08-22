package com.example.spring.data.elasticsearch;


import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.elasticsearch.config.DemoDataPopulatorConfig;
import com.example.spring.data.elasticsearch.model.Conference;
import com.example.spring.data.elasticsearch.model.Movie;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.util.Assert;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = {
    ElasticsearchApplication.class,
    DemoDataPopulatorConfig.class,
    ElasticsearchOperationsTest.TestConfiguration.class
})
@Testcontainers
class ElasticsearchOperationsTest {

  private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
  @Container //
  private static final ElasticsearchContainer container = new ElasticsearchContainer(
      DockerImageName.parse(DockerImages.ELASTICSEARCH)) //
      .withPassword("foobar") //
      .withReuse(true);

  @Configuration
  static class TestConfiguration extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {

      Assert.notNull(container, "TestContainer is not initialized!");

      return ClientConfiguration.builder() //
          .connectedTo(container.getHttpHostAddress()) //
          .usingSsl(container.createSslContextFromCa()) //
          .withBasicAuth("elastic", "foobar") //
          .build();
    }
  }

  @Autowired
  ElasticsearchOperations operations;

  @Test
  void textSearch() throws ParseException {
    var expectedDate = "2014-10-29";
    var expectedWord = "java";
    var query = new CriteriaQuery(
        new Criteria("keywords").contains(expectedWord)
            .and(new Criteria("date").greaterThanEqual(expectedDate)));

    var result = operations.search(query, Conference.class);

    assertThat(result).hasSize(3);

    for (var conference : result) {
      assertThat(conference.getContent().getKeywords()).contains(expectedWord);
      assertThat(format.parse(conference.getContent().getDate()))
          .isAfter(format.parse(expectedDate));
    }
  }

  @Test
  void geoSpatialSearch() {

    var startLocation = new GeoPoint(50.0646501D, 19.9449799D);
    var range = "530km"; // or 530km
//    var range = "330mi"; // or 530km
    var query = new CriteriaQuery(new Criteria("location").within(startLocation, range));

    var result = operations.search(query, Movie.class, IndexCoordinates.of("conference-index"));

    assertThat(result).hasSize(2);
  }
}