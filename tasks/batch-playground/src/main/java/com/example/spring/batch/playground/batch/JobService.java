package com.example.spring.batch.playground.batch;

import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

  private final JobRepository jobRepository;
  private JobLauncher asyncJobLauncherInstance = null;

  /**
   * Non-default JobLauncher instance, using async TaskExecutor.
   * <p><b>Note:</b> Just for test purposes, it's better to use a single configured JobLauncher.</p>
   * @see #newAsyncJobLauncher()
   */
  public JobLauncher asyncJobLauncherInstance() {
    if (asyncJobLauncherInstance == null) {
      asyncJobLauncherInstance = newAsyncJobLauncher();
    }
    return asyncJobLauncherInstance;
  }

  /**
   * Create a JobLauncher using async TaskExecutor
   */
  public JobLauncher newAsyncJobLauncher() {
    try {
      TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
      jobLauncher.setJobRepository(jobRepository);
      jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
      jobLauncher.afterPropertiesSet();
      return jobLauncher;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

}
