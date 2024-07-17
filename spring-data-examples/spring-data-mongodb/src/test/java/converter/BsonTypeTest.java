package converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoField;
import lombok.extern.log4j.Log4j2;
import org.bson.BsonDateTime;
import org.junit.jupiter.api.Test;

@Log4j2
class BsonTypeTest {

  @Test
  void bsonDateType() {
    Instant instant = Instant.now().with(ChronoField.MILLI_OF_SECOND, 0);
    log.info("{}", instant);

    BsonDateTime bsonDateTime = new BsonDateTime(instant.toEpochMilli());
    log.info("{}", bsonDateTime);

    Instant convertedInstant = Instant.ofEpochMilli(bsonDateTime.getValue());
    log.info("{}", convertedInstant);
    assertThat(convertedInstant).isEqualTo(instant);
  }

}
