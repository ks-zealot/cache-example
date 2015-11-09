package com.a1s.cache.config;

import com.a1s.cache.exception.NotYetStartedException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Created by y.lybarskiy on 09.11.2015.
 */
public class Config {
  private static Configuration config;
    public static void init() throws ConfigurationException {
        config  = new PropertiesConfiguration("cache.properties");
    }

    public static String getStringProperty(String name) {
      return  config.getString(name);
    }
    public static Integer getIntProperty (String name) {
        return config.getInt(name);
    }

    public static long getLongProperty(String name) {
        return config.getLong(name);
    }
}
