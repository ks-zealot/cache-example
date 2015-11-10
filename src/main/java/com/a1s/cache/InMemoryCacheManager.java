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
    public   Serializable getObject(Object key) {
        return (Serializable) m.get(key);
    }

    @Override
    public void clear() {
        m.clear();
    }

    @Override
    public boolean delete(Object key) {
        if (ifExist(key)){
            m.remove(key);
            return true;
        } else{
            return false;
        }


    }

    @Override
    public boolean ifExist(Object key) {
       return m.containsKey(key);
    }


}
