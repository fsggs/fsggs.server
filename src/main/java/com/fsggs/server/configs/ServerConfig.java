package com.fsggs.server.configs;

import com.fsggs.server.Application;
import com.fsggs.server.utils.FileUtils;

import java.io.*;
import java.util.*;

class ServerConfig {
    final private String serverConfigFile = "server.properties";

    final private Properties properties = new Properties();
    final private Map<String, String> config = new LinkedHashMap<>();

    ServerConfig() {
        try {
            if (FileUtils.isRunnedInJar()) {
                loadConfig();
            } else {
                loadConfig(true);
            }
        } catch (FileNotFoundException e1) {
            try {
                loadConfig(true).saveConfig();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Enumeration enuKeys = properties.keys();
        while (enuKeys.hasMoreElements()) {
            String key = (String) enuKeys.nextElement();
            config.put(key, properties.getProperty(key));
        }
    }

    private ServerConfig saveConfig() throws IOException {
        File configFile = new File(serverConfigFile);
        OutputStream outputStream = new FileOutputStream(FileUtils.getApplicationPath().toString() + '/' + configFile);
        properties.store(outputStream, "Server config");
        outputStream.close();

        return this;
    }

    private ServerConfig loadConfig() throws IOException {
        return this.loadConfig(false);
    }

    private ServerConfig loadConfig(boolean inJar) throws IOException {
        File configFile = new File(serverConfigFile);
        Application.logger.info("Try load " + serverConfigFile + " from " + (inJar ? "jar" : "outside jar"));

        InputStream inputStream;
        if (inJar) {
            inputStream = ClassLoader.getSystemResourceAsStream(serverConfigFile);
        } else {
            inputStream = new FileInputStream(FileUtils.getApplicationPath().toString() + '/' + configFile);
        }
        properties.load(inputStream);
        inputStream.close();

        return this;
    }

    ServerConfig addPropertyToServerConfig(String key, String value) {
        try {
            properties.setProperty(key, value);
            config.put(key, value);
            saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    Map<String, String> getConfig() {
        return config;
    }
}
