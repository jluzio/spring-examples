package com.example.spring.batch.playground.user_posts.api.model;

import com.example.spring.batch.playground.user_posts.persistence.model.User;
import java.util.List;

public record ImportDataJobRequest(String runId, List<User> users) {

}
