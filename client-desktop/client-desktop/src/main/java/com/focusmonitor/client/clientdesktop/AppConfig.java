package com.focusmonitor.client.clientdesktop;

import java.io.InputStream;
import java.util.Properties;
import java.util.prefs.Preferences;

public class AppConfig {
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final String PREF_BASE_URL_KEY = "apiBaseUrl";
    private static final Properties props = new Properties();
    private static final Preferences prefs = Preferences.userNodeForPackage(AppConfig.class);

    static {
        try
                (InputStream is = AppConfig.class.getResourceAsStream("/config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static String getBaseURL(){
        String fromPrefs = trimToNull(prefs.get(PREF_BASE_URL_KEY, null));
        if (fromPrefs != null) {
            return fromPrefs;
        }

        String fromSystemProperty = trimToNull(System.getProperty("focusmonitor.api.base.url"));
        if (fromSystemProperty != null) {
            return fromSystemProperty;
        }

        String fromEnvironment = trimToNull(System.getenv("API_BASE_URL"));
        if (fromEnvironment != null) {
            return fromEnvironment;
        }

        String fromConfig = trimToNull(props.getProperty("api.base.url"));
        if (fromConfig != null) {
            return fromConfig;
        }

        return DEFAULT_BASE_URL;
    }

    public static void setBaseURL(String baseURL) {
        String normalized = trimToNull(baseURL);
        if (normalized == null) {
            prefs.remove(PREF_BASE_URL_KEY);
        } else {
            prefs.put(PREF_BASE_URL_KEY, normalized);
        }
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
