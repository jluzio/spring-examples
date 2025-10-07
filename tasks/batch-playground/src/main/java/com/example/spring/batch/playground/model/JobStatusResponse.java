package com.example.spring.batch.playground.model;

import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameters;

@Builder(toBuilder = true)
public record JobStatusResponse(Long jobId, BatchStatus status, LocalDateTime startTime,
                                LocalDateTime createTime, LocalDateTime endTime, JobParameters jobParameters) {

}
