package com.example.spring.framework.api.model;

import com.example.spring.framework.api.model.view.Public;
import com.fasterxml.jackson.annotation.JsonView;

@JsonView(Public.class)
public interface ModelPublicView {

}
