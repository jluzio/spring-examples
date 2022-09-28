package com.example.liquibase.tools.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleResourceLoaderResourceAccessor /* extends AbstractResourceAccessor */ {

  private final ResourceLoader resourceLoader;


//  @Override
  public Set<InputStream> getResourcesAsStream(String path) throws IOException {
    return Set.of(resourceLoader.getResource(path).getInputStream());
  }

//  @Override
  public Set<String> list(String relativeTo, String path, boolean includeFiles,
      boolean includeDirectories, boolean recursive) throws IOException {
    return Set.of(resourceLoader.getResource(path).getFile().getAbsolutePath());
  }

//  @Override
  public ClassLoader toClassLoader() {
    return this.getClass().getClassLoader();
  }
}
