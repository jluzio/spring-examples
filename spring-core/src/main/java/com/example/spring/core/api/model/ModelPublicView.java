package com.example.spring.core.api.model;

import com.example.spring.core.api.model.view.Public;
import com.fasterxml.jackson.annotation.JsonView;

@JsonView(Public.class)
public interface ModelPublicView {

}
