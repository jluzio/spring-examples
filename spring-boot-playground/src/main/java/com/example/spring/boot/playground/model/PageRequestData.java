package com.example.spring.boot.playground.model;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class PageRequestData {

  private int page;
  private int size;
  private Sort sort;

  public Pageable toPageable() {
    if (size <= 0) {
      return Pageable.unpaged();
    } else if (sort != null) {
      return PageRequest.of(page, size, sort);
    } else {
      return PageRequest.of(page, size);
    }
  }
}
