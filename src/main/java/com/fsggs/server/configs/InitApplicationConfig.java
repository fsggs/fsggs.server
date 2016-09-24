package com.fsggs.server.configs;

import com.fsggs.server.Application;
import com.fsggs.server.core.FrameworkRegistry;

public class InitApplicationConfig {

    public InitApplicationConfig() {

        ServerConfig sg = new ServerConfig();
        ServerConfigYML.Config sgy = (new ServerConfigYML()).getConfig();
        Application.serverConfig = sg.getConfig();
        Application.registry = new FrameworkRegistry();

        // ssl
        if (Application.serverConfig.containsKey("ssl")) {
            Application.SSL = Boolean.parseBoolean(Application.serverConfig.get("ssl"));
        } else {
            Application.SSL = System.getProperty("ssl") != null;
            sg.addPropertyToServerConfig("ssl", Boolean.toString(Application.SSL));
        }

        // port
        if (Application.serverConfig.containsKey("port")) {
            Application.PORT = Integer.parseInt(Application.serverConfig.get("port"));
        } else {
            Application.PORT = Integer.parseInt(Application.SSL ? "32543" : "32500");
            sg.addPropertyToServerConfig("port", Integer.toString(Application.PORT));
        }

        // ip
        if (Application.serverConfig.containsKey("ip")) {
            Application.IP = Application.serverConfig.get("ip");
        } else {
            Application.IP = "0.0.0.0";
            sg.addPropertyToServerConfig("ip", Application.IP);
        }

        // gate
        if (Application.serverConfig.containsKey("gate")) {
            Application.WEBSOCKET_PATH = Application.serverConfig.get("gate");
        } else {
            Application.WEBSOCKET_PATH = "";
            sg.addPropertyToServerConfig("gate", Application.WEBSOCKET_PATH);
        }

        // ssl
        if (Application.serverConfig.containsKey("client_url")) {
            Application.CLIENT_URL = (Application.serverConfig.get("client_url"));
        } else {
            Application.CLIENT_URL = "*";
            sg.addPropertyToServerConfig("client_url", Application.CLIENT_URL);
        }
    }
}
