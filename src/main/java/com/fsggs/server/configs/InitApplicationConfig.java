package com.fsggs.server.configs;

import com.fsggs.server.Application;
import com.fsggs.server.core.network.NetworkPacketRegister;

public class InitApplicationConfig {

    public InitApplicationConfig(Application application) {

        ServerConfig sg = new ServerConfig();
        application.serverConfig = sg.getConfig();
        Application.networkPackets = new NetworkPacketRegister().getNetworkPackets();

        // ssl
        if (application.serverConfig.containsKey("ssl")) {
            Application.SSL = Boolean.parseBoolean(application.serverConfig.get("ssl"));
        } else {
            Application.SSL = System.getProperty("ssl") != null;
            sg.addPropertyToServerConfig("ssl", Boolean.toString(Application.SSL));
        }

        // port
        if (application.serverConfig.containsKey("port")) {
            Application.PORT = Integer.parseInt(application.serverConfig.get("port"));
        } else {
            Application.PORT = Integer.parseInt(Application.SSL ? "32543" : "32500");
            sg.addPropertyToServerConfig("port", Integer.toString(Application.PORT));
        }

        // ip
        if (application.serverConfig.containsKey("ip")) {
            Application.IP = application.serverConfig.get("ip");
        } else {
            Application.IP = "0.0.0.0";
            sg.addPropertyToServerConfig("ip", Application.IP);
        }

        // gate
        if (application.serverConfig.containsKey("gate")) {
            Application.WEBSOCKET_PATH = application.serverConfig.get("gate");
        } else {
            Application.WEBSOCKET_PATH = "";
            sg.addPropertyToServerConfig("gate", Application.WEBSOCKET_PATH);
        }
    }
}
