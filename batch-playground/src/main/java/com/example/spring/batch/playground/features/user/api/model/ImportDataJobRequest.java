package com.example.spring.batch.playground.features.user.api.model;

import com.example.spring.batch.playground.features.user.persistence.model.User;
import java.util.List;

public record ImportDataJobRequest(String runId, List<User> users, boolean async) {

}
