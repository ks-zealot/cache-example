package com.a1s.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by y.lybarskiy on 09.11.2015.
 */
public class InMemoryCacheManager implements CacheManager {
    private Map m = new ConcurrentHashMap<>();//inmemorycache
    @Override
    public  void putObject(Object key, Serializable object) {
        m.put(key, object);
    }



    @Override
    public   Object getObject(Object key) {
        return m.get(key);
    }

    @Override
    public void clear() {
        m.clear();
    }

    @Override
    public void delete(Object key) {
        m.remove(key);
    }

    @Override
    public boolean ifExist(Object key) {
       return m.containsKey(key);
    }


}
