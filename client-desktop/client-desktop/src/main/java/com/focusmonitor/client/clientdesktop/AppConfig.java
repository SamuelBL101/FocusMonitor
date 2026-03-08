package com.focusmonitor.client.clientdesktop;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties props = new Properties();

    static {
        try
                (InputStream is = AppConfig.class.getResourceAsStream("/config.properties")) {
            props.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static String getBaseURL(){
        return props.getProperty("api.base.url");
    }
}
