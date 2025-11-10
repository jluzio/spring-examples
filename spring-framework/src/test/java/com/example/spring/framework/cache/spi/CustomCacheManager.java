package com.example.spring.framework.cache.spi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Simple CacheManager.
 * <p>See other implementations for reference, such as {@link org.springframework.cache.concurrent.ConcurrentMapCacheManager}</p>
 */
public class CustomCacheManager implements CacheManager {

  public static final String DEFAULT_CACHE = "#default";
  private final Collection<String> cacheNames;
  private final Map<String, CustomCache> cacheMap;
  private final boolean useDefaultCache;

  public CustomCacheManager(Collection<String> cacheNames) {
    this.cacheNames = cacheNames;
    this.cacheMap = cacheNames.stream()
        .map(it -> Map.entry(it, new CustomCache(it)))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    this.useDefaultCache = cacheNames.equals(List.of(DEFAULT_CACHE));
  }

  public CustomCacheManager() {
    this(List.of(DEFAULT_CACHE));
  }

  @Override
  public Cache getCache(String name) {
    return useDefaultCache
        ? cacheMap.get(DEFAULT_CACHE)
        : cacheMap.get(name);
  }

  @Override
  public Collection<String> getCacheNames() {
    return cacheNames;
  }
}
