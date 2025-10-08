package com.example.liquibase.tools.util;

import java.io.IOException;
import java.util.List;
import liquibase.resource.AbstractResourceAccessor;
import liquibase.resource.Resource;
import liquibase.resource.URIResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@Slf4j
//@RequiredArgsConstructor
public class SimpleResourceLoaderResourceAccessor extends AbstractResourceAccessor {

  private final ResourceLoader resourceLoader;

  public SimpleResourceLoaderResourceAccessor(ResourceLoader resourceLoader) {
    super();
    this.resourceLoader = resourceLoader;
    log.debug("SimpleResourceLoaderResourceAccessor<>");
  }

  @Override
  public List<Resource> search(String path, boolean recursive) throws IOException {
    log.debug("SimpleResourceLoaderResourceAccessor::search :: {} | {}", path, recursive);
    return getAll(path);
  }

  @Override
  public List<Resource> getAll(String path) throws IOException {
    log.debug("SimpleResourceLoaderResourceAccessor::getAll :: {}", path);
    Resource resource = new URIResource(
        path, resourceLoader.getResource(path).getURI());
    return List.of(resource);
  }

  @Override
  public List<String> describeLocations() {
    log.debug("SimpleResourceLoaderResourceAccessor::describeLocations");
    return List.of("classpath:");
  }

  @Override
  public void close() throws Exception {
    // empty
  }

}
