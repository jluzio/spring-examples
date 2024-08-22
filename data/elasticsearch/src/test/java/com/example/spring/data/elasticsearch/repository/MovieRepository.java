package com.example.spring.data.elasticsearch.repository;

import com.example.spring.data.elasticsearch.model.Movie;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MovieRepository extends ElasticsearchRepository<Movie, String> {

}