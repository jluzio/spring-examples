package com.example.spring.batch.playground.features.resilience.persistence.repository;

import com.example.spring.batch.playground.features.resilience.persistence.model.JobData;
import org.springframework.data.repository.CrudRepository;

public interface JobDataRepository extends CrudRepository<JobData, String> {

}
