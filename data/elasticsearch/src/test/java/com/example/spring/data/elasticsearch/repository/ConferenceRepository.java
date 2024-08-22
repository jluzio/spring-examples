package com.example.spring.data.elasticsearch.repository;

import com.example.spring.data.elasticsearch.model.Conference;
import com.example.spring.data.elasticsearch.model.Movie;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ConferenceRepository extends ElasticsearchRepository<Conference, String> {

}