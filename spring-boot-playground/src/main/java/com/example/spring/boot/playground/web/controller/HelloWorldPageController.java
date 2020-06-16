package com.example.spring.boot.playground.web.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloWorldPageController {
	
	@RequestMapping("/pages/hello/{name}")
	public String hello(Map<String, Object> model, @PathVariable("name") String name) {
		model.put("name", name);
		return "hello";
	}

}
