package com.example.spring.data.mongodb.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import lombok.extern.log4j.Log4j2;
import org.bson.BsonDateTime;
import org.junit.jupiter.api.Test;

@Log4j2
class OffsetDateTimeValueConverterTest {

  OffsetDateTimeValueConverter converter = new OffsetDateTimeValueConverter();

  @Test
  void converter_roundtrip() {

    OffsetDateTime source = OffsetDateTime.now().with(ChronoField.MILLI_OF_SECOND, 0);
    log.info("{}", source);

    BsonDateTime converted = converter.write(source, null);
    BsonDateTime expectedConverted = new BsonDateTime(source.toInstant().toEpochMilli());
    log.info("{}", converted);
    assertThat(converted)
        .isEqualTo(expectedConverted);

    OffsetDateTime convertedSource = converter.read(converted, null);
    log.info("{}", convertedSource);
    assertThat(convertedSource)
        .isEqualTo(source);
  }

}
