package com.example.spring.data.mongodb.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("products")
public record Product(String name, double price, String description) {

}
