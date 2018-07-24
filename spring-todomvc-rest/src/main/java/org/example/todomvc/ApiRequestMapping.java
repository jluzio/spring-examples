package org.example.todomvc;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestMapping;

@Retention(RUNTIME)
@Target(TYPE)
@RequestMapping(value="/api")
public @interface ApiRequestMapping {

}
