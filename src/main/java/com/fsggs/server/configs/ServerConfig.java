package com.fsggs.server.configs;

import com.fsggs.server.Application;
import com.fsggs.server.utils.FileUtils;

import java.io.*;
import java.util.*;

public class ServerConfig {
    final private String serverConfigFile = "server.properties";

    final private Properties properties = new Properties();
    final private Map<String, String> config = new LinkedHashMap<>();

    public ServerConfig() {
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

    public ServerConfig saveConfig() throws FileNotFoundException, IOException {
        File configFile = new File(serverConfigFile);
        OutputStream outputStream = new FileOutputStream(FileUtils.getApplicationPath().toString() + '/' + configFile);
        properties.store(outputStream, "Server config");
        outputStream.close();

        return this;
    }

    public ServerConfig loadConfig() throws FileNotFoundException, IOException {
        return this.loadConfig(false);
    }

    public ServerConfig loadConfig(boolean inJar) throws FileNotFoundException, IOException {
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

    public ServerConfig addPropertyToServerConfig(String key, String value) {
        try {
            properties.setProperty(key, value);
            config.put(key, value);
            saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Map<String, String> getConfig() {
        return config;
    }
}
