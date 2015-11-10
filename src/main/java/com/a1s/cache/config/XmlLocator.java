package com.a1s.cache.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by y.lybarskiy on 10.11.2015.
 */
public class XmlLocator {
private Logger log = LoggerFactory.getLogger(XmlLocator.class);
    public String locate(){
        File f = new File(".");
        log.debug("looking for config file in start dir {}", f.getAbsolutePath());
        String result = search(f.listFiles());
        if ( result != null){
            return result;
        }
        File home =  new File(System.getProperty("user.home"));
        log.debug("looking for config file in home dir {}",home.getAbsolutePath() );
        result = search(home.listFiles());
        return result;

    }

    private String search (File[] listFiles) {
        String result = null;
        for (File file : listFiles){
            if (file.getName().equals("cache.xml")){
                result =  file.getAbsolutePath();
            }
        }
        return result;
    }


}
