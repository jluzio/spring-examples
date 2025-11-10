package com.example.spring.framework.cache.spi;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cache.support.AbstractValueAdaptingCache;

/**
 * Simple cache, simplified version of ConcurrentMapCache.
 * <p>See other implementations for reference, such as {@link org.springframework.cache.concurrent.ConcurrentMapCache}</p>
 * <p>For serialization see {@link org.springframework.cache.concurrent.ConcurrentMapCache} and {@link org.springframework.core.serializer.support.SerializationDelegate}</p>
 */
public class CustomCache extends AbstractValueAdaptingCache {

  private final String name;
  private final ConcurrentHashMap<Object, Object> store;

  public CustomCache(String name) {
    this(name, true);
  }

  public CustomCache(String name, boolean allowNullValues) {
    super(allowNullValues);
    this.name = name;
    this.store = new ConcurrentHashMap<>(256);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Object getNativeCache() {
    return store;
  }

  @Override
  protected Object lookup(Object key) {
    return store.get(key);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T get(Object key, Callable<T> valueLoader) {
    return (T) fromStoreValue(store.computeIfAbsent(key, k -> {
      try {
        return toStoreValue(valueLoader.call());
      } catch (Throwable e) {
        throw new ValueRetrievalException(k, valueLoader, e);
      }
    }));
  }

  @Override
  public void put(Object key, Object value) {
    store.put(key, toStoreValue(value));
  }

  @Override
  public void evict(Object key) {
    store.remove(key);
  }

  @Override
  public void clear() {
    store.clear();
  }
}
