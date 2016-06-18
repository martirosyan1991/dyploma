package com.pkmpei.mobile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Harry on 23.05.2016.
 */
public class PreStudent {

    Map<String, String> properties = new HashMap<>();

    public String getProperty(String name) {
        return  properties.get(name);
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    public Map<String, String> getProperties() {
        if (properties == null) {
            return new HashMap<>();
        } else {
            return properties;
        }
    }

    public void clearProperties() {
        properties.clear();
    }
}
