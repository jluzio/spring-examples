package com.example.spring.shell.cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class WebCommands {

  @ShellMethod("Get url contents.")
  public String webGet(String url) throws IOException {
    return getContentsOfUrlAsString(url);
  }

  @ShellMethod("Save url contents.")
  public String webSave(String url, String file) throws IOException {
    String contents = getContentsOfUrlAsString(url);
    try (PrintWriter out = new PrintWriter(file)) {
      out.write(contents);
    }
    return "Done.";
  }

  private String getContentsOfUrlAsString(String url) throws IOException {
    try (InputStream inputStream = new URL(url).openStream()) {
      return IOUtils.toString(inputStream);
    }
  }

}