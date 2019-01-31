package org.example.work;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkWagesFileRenamerTest {
  @Autowired
  private WorkWagesFileRenamer workWagesFileRenamer;

  @Configuration
  @ComponentScan(basePackages = "org.example")
  static class Config {
  }

  @Test
  public void run() throws IOException {
    Path inputPath = Paths.get("C:\\Users\\jluzio\\Desktop\\vencimentos");
    Path outputPath = inputPath.resolve("output");
    workWagesFileRenamer.run(inputPath, outputPath);
  }

}