package com.example.spring.shell.cmd;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class GreetCommands {

  @ShellMethod("Say hello.")
  public String greet(@ShellOption(defaultValue="World") String who) {
    return "Hello " + who;
  }

}
