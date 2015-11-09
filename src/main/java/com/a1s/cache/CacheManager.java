package com.a1s.cache;

import java.io.Serializable;

/**
 * Created by y.lybarskiy on 09.11.2015.
 */
public interface  CacheManager <K, V extends Serializable> {
      void  putObject(K key, V object);
      Object getObject(K key);
      void clear();
      void delete(K key);
      boolean ifExist(K key);
}
