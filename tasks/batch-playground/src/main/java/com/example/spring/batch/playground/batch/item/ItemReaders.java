package com.example.spring.batch.playground.batch.item;

import java.util.Arrays;
import lombok.experimental.UtilityClass;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;

@UtilityClass
public class ItemReaders {

  public static ItemReader<String> csv(String value) {
    return new ListItemReader<>(Arrays.stream(value.split(",")).toList());
  }

}
