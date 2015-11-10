package com.a1s.cache.config;

/**
 * Created by y.lybarskiy on 10.11.2015.
 */
public class CacheBean {
   private int maxFirstLevelSize = 0;
    private int maxSecondLevelSize = 0;
    private int delay = 0;
    private long ttl = 0l;
    private String path;

    public int getMaxFirstLevelSize() {

        return maxFirstLevelSize;
    }

    public void setMaxFirstLevelSize(int maxFirstLevelSize) {
        this.maxFirstLevelSize = maxFirstLevelSize;
    }

    public int getMaxSecondLevelSize() {
        return maxSecondLevelSize;
    }

    public void setMaxSecondLevelSize(int maxSecondLevelSize) {
        this.maxSecondLevelSize = maxSecondLevelSize;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return  "{" +
                "path='" + path + '\'' +
                ", ttl=" + ttl +
                ", delay=" + delay +
                ", maxSecondLevelSize=" + maxSecondLevelSize +
                ", maxFirstLevelSize=" + maxFirstLevelSize +
                '}';
    }
}
