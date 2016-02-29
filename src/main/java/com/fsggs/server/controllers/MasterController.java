package com.fsggs.server.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseController;
import com.fsggs.server.core.network.Controller;
import com.fsggs.server.core.network.Route;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Controller
public class MasterController extends BaseController {

    @Route(PATH = "/api/getVersion.json", METHOD = "GET", TYPE = "application/json; charset=UTF-8")
    public String getVersion() {
        ObjectMapper mapper = new ObjectMapper();
        VersionJSON versionJSON = new VersionJSON();

        try {
            return mapper.writeValueAsString(versionJSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    private class VersionJSON implements Serializable {
        @JsonProperty
        String name = Application.APPLICATION_NAME;

        @JsonProperty
        String version = Application.APPLICATION_VERSION + '.' + Application.APPLICATION_VERSION_ID;

        @JsonProperty
        String status = "online";

        @JsonProperty
        boolean emulationAPI = false;

        @JsonProperty
        List<Server> servers = new LinkedList<>();

        public VersionJSON() {
            servers.add(new Server("Test server", "ws://127.0.0.1:32500"));
        }

        private class Server implements Serializable {
            @JsonProperty
            public String name;

            @JsonProperty
            public String address;

            public Server(String name, String address) {
                this.name = name;
                this.address = address;
            }
        }
    }
}
