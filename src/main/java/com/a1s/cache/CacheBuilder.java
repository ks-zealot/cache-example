package com.a1s.cache;

import com.a1s.cache.config.CacheBean;

/**
 * Created by zealot on 10.11.2015.
 */
public final class CacheBuilder {
    private long ttl =0l;
    private int delay= 10;
    private int maxFirstLevelSize =10;
    private int maxSecondLevelSize = 10;
    private String path = "tmp";

    private CacheBuilder() {
    }

    public CacheBuilder getBuilder() {
        return new CacheBuilder();
    }

    public CacheBuilder setTtl(long ttl) {
        this.ttl = ttl;
        return this;
    }

    public CacheBuilder setDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public CacheBuilder setMaxFirstLevelSize(int maxFirstLevelSize) {
        this.maxFirstLevelSize = maxFirstLevelSize;
        return this;
    }

    public CacheBuilder setMaxSecondLevelSize(int maxSecondLevelSize) {
        this.maxSecondLevelSize = maxSecondLevelSize;
        return this;
    }

    public CacheBuilder setPath(String path) {
        this.path = path;
        return this;
    }
/*
build cache manager implementation from passed params
 */
    public CacheManagerImpl build() {
        CacheBean bean = new CacheBean();
        bean.setDelay(delay);
        bean.setMaxFirstLevelSize(maxFirstLevelSize);
        bean.setMaxSecondLevelSize(maxSecondLevelSize);
        bean.setPath(path);
        bean.setTtl(ttl);
        return Cache.getInstance().getCache(bean);
    }

}
