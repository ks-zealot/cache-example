package com.a1s.cache;

import com.a1s.cache.config.CacheBean;
import com.a1s.cache.config.XmlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by y.lybarskiy on 10.11.2015.
 */
public final class Cache {
    private static boolean isInit = false;
    private XmlConfig config;
    private static Logger log = LoggerFactory.getLogger(Cache.class);
    private Map<String, CacheManagerImpl> cachces = new ConcurrentHashMap<>();
    private CacheBean defaultBean;
    private static Cache cache = new Cache();
    private Cache(){

    }
    public static synchronized Cache getInstance ()
    {
        return cache;
    }

    /**
     * get named cache. if cache retrieveed at first time, class perfrom configuration
     * return default cache if no such cache described in xml file
     * @param name
     * @return
     * @throws ParserConfigurationException if errors perform during parse xml
     * @throws SAXException xml file corrupt or not created properly
     * @throws IOException if could not find or load file
     */
    public synchronized CacheManager getCache(String name) throws   ParserConfigurationException, SAXException, IOException {
        if (!isInit) {
            config = new XmlConfig();
            config.init();
            defaultBean = new CacheBean();
            defaultBean.setPath("tmp");
            defaultBean.setTtl(1000);
            defaultBean.setDelay(1000);
            defaultBean.setMaxSecondLevelSize(100);
            defaultBean.setMaxFirstLevelSize(10);
            isInit = true;
        }
        CacheManagerImpl manager = cachces.get(name);
        if (manager == null) {
            log.debug("init manager {}", name);

            CacheBean bean =  config.getCacheBean(name);
            if (bean == null) {
                log.debug("no such config for name {}, create cache manager with default configuration {}" ,defaultBean);
                cachces.put(name, getCache());
            } else {
                log.debug("create cache manager with configuration {}", bean);
                cachces.put(name, createManager(bean));
            }
            manager = cachces.get(name);
            manager.init();
        }
            return manager;

    }
    /*
    get cache with default parameters
     */
    public CacheManagerImpl getCache() {
       return createManager(defaultBean);

    }
    /*
    return cache bean implementation from passed bean
     */
protected CacheManagerImpl getCache(CacheBean bean){
    return createManager(bean);
}
    /**
     * create cache manager from bean configuration
     * @param bean configuration bean
     * @return new cache manager implementation
     */
    private CacheManagerImpl createManager(CacheBean bean) {
        CacheManagerImpl manager  = new CacheManagerImpl();
        manager.setDelay(bean.getDelay());
        manager.setTtl(bean.getTtl());
        manager.setMaxSizeSecondLevel(bean.getMaxSecondLevelSize());
        manager.setMaxSizeFirstLevel(bean.getMaxFirstLevelSize());
        manager.setPath(bean.getPath());
        manager.init();
        return manager;
    }
}
