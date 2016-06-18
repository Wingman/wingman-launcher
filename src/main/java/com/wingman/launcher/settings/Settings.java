package com.wingman.launcher.settings;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Settings {

    public static final Path HOME_DIR = Paths.get(System.getProperty("user.home")).resolve("Wingman");
    public static final File SETTINGS_FILE = HOME_DIR.resolve("launcher.properties").toFile();

    public static final String DOWNLOAD_DEFAULT_PLUGINS = "download_default_plugins";

    private Properties properties = new Properties();

    public Settings() {
        if (SETTINGS_FILE.exists()) {
            try {
                properties.load(new FileReader(SETTINGS_FILE));
            } catch (IOException e) {
                Throwables.propagate(e);
            }
        }

        ImmutableMap.Builder<String, Object> defaultPropertiesBuilder = ImmutableMap.<String, Object>builder()
                .put(DOWNLOAD_DEFAULT_PLUGINS, "true");

        Map<String, Object> defaultProperties = defaultPropertiesBuilder.build();

        if (properties.isEmpty()) {
            for (Map.Entry<String, Object> e : defaultProperties.entrySet()) {
                properties.put(e.getKey(), e.getValue());
            }
        } else {
            for (Map.Entry<String, Object> e : defaultProperties.entrySet()) {
                if (!properties.containsKey(e.getKey())) {
                    properties.put(e.getKey(), e.getValue());
                }
            }

            Set<String> keysToRemove = new HashSet<>();
            for (Map.Entry<Object, Object> e : properties.entrySet()) {
                String key = (String) e.getKey();
                if (!defaultProperties.containsKey(key)) {
                    keysToRemove.add(key);
                }
            }
            for (String k : keysToRemove) {
                properties.remove(k);
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                save();
            }
        });
    }

    private void save() {
        try {
            properties.store(new FileWriter(SETTINGS_FILE), "Wingman launcher settings");
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

    public void update(String key, String value) {
        properties.put(key, value);
    }

    public void updateBoolean(String key, Boolean value) {
        update(key, value ? "true" : "false");
    }

    public String get(String key) {
        return (String) properties.get(key);
    }

    public boolean getBoolean(String key) {
        return properties.get(key).equals("true");
    }

    public int getInteger(String key) {
        return Integer.parseInt((String) properties.get(key));
    }
}
