package com.a1s.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by y.lybarskiy on 09.11.2015.
 */
public final class CacheManagerImpl implements CacheManager {
    private final Logger log = LoggerFactory.getLogger(CacheManagerImpl.class);
    private int maxSizeFirstLevel = 0;
    private int maxSizeSecondLevel = 0;
    private InMemoryCacheManager inMemoryCacheManager;
    private FileSysCacheManager fileSysCacheManager;
    private final AtomicInteger inMemorySize = new AtomicInteger(0);
    private final AtomicInteger onDiskSize = new AtomicInteger(0);
    private final Map<Object, Date> expiredMap = new ConcurrentHashMap<>();
    private long ttl = 0;
    private final ScheduledExecutorService clearer = Executors.newSingleThreadScheduledExecutor();
    private int delay = 0;
    private AtomicBoolean isStarted = new AtomicBoolean(false);
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    protected CacheManagerImpl() {

    }

    private String path;

    protected void setPath(String path) {
        this.path = path;
    }

    protected void setDelay(int delay) {
        this.delay = delay;
    }

    protected void setMaxSizeFirstLevel(int maxSizeFirstLevel) {
        this.maxSizeFirstLevel = maxSizeFirstLevel;
    }

    protected void setMaxSizeSecondLevel(int maxSizeSecondLevel) {
        this.maxSizeSecondLevel = maxSizeSecondLevel;
    }

    /*
    init cache manager, all inner resources and start support thread
     */
    public void init() {
        File innerStore = new File(path);
        if (!innerStore.exists()) {
            innerStore.mkdirs();
        }
        if (inMemoryCacheManager == null) {
            inMemoryCacheManager = new InMemoryCacheManager();
        }
        if (fileSysCacheManager == null) {
            if (path != null) {
                fileSysCacheManager = new FileSysCacheManager(this.path);
            } else {
                fileSysCacheManager = new FileSysCacheManager("tmp");
            }

        }
        log.info("start cache with params : maxSizeFirstLevel {}, maxSizeSecondLevel {}, ttl {}, fs path to store {}, delay",
                maxSizeFirstLevel, maxSizeSecondLevel, ttl, path, delay);
        clearer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.debug("start clear task");
                try {
                    for (Object key : expiredMap.keySet()) {
                        Date d = expiredMap.get(key);
                        long now = System.currentTimeMillis();
                        if (now - d.getTime() > ttl) {
                            log.debug(" object with key {} expired because {} - {} > {}", key, now, d.getTime(), ttl);
                            delete(key);
                        }
                    }
                } catch (Throwable t) {
                    log.error("error", t);
                }
            }
        }, 0, delay, TimeUnit.SECONDS);
        isStarted.compareAndSet(false, true);
    }
/*
stop cache, shut down threads
 */
    public void stop() {
        isStarted.compareAndSet(true, false);
        clear();
        clearer.shutdown();
        fileSysCacheManager.shutdown();
    }
/*
put object in cache

 */
    @Override
    public void putObject(Object key, Serializable object) {
        if (!isStarted.get()){
            log.debug("cache already stopped, drop object with key {}", key);
            return;
        }
        log.debug("put object with key {}", key);
        if (inMemorySize.get() < maxSizeFirstLevel) {
            inMemorySize.incrementAndGet();
            log.debug("put object inmemory level");
            inMemoryCacheManager.putObject(key, object);
            expiredMap.put(key, new Date());
            log.debug("put object key {} in expiration map", key);
        } else if (onDiskSize.get() < maxSizeSecondLevel) {
            log.debug("inmemory cache overloaded");
            onDiskSize.incrementAndGet();
            log.debug("put object in file level");
            fileSysCacheManager.putObject(key, object);
            expiredMap.put(key, new Date());
            log.debug("put object key {} in expiration map", key);
        } else {
            log.debug("all caches overloaded, object with key {} was dropped", key);
        }
    }
/*

get object for passed key or null if object does not exist
 */
    @Override
    public Serializable getObject(Object key) {
        Serializable o = inMemoryCacheManager.getObject(key);
        if (o == null) {
            log.debug("retrieve object with key {} from fs cache", key);
            o = fileSysCacheManager.getObject(key);
        } else {
            log.debug("retrieve object with key {} from inmemory cache", key);
        }
        return o;
    }

    @Override
    public void clear() {
        inMemoryCacheManager.clear();
        fileSysCacheManager.clear();
    }
/*
delete from cache object with specific key
 */
    @Override
    public boolean delete(Object key) {
        if (inMemoryCacheManager.ifExist(key)) {
            log.debug("delete object with key {} from inmemory cache", key);
            return inMemoryCacheManager.delete(key);
        } else if (fileSysCacheManager.ifExist(key)) {
            log.debug("delete object with key {} from fs cache", key);
            return fileSysCacheManager.delete(key);
        } else {
            return false;
        }

    }
/*
retuen true if object with this key exist, false otherwise

 */
    @Override
    public boolean ifExist(Object key) {
        return inMemoryCacheManager.ifExist(key) || fileSysCacheManager.ifExist(key);
    }


}
