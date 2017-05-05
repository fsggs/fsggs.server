package com.fsggs.server.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseController;
import com.fsggs.server.core.network.Controller;
import com.fsggs.server.core.network.Route;
import com.fsggs.server.models.master.ServerEntity;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN;

@Controller
public class MasterController extends BaseController {

    @Route(PATH = "/api/getVersion.json", METHOD = "GET", TYPE = "application/json; charset=UTF-8")
    public String getVersion() {
        int limit = 10;
        int offset = (params.containsKey("page")) ? Integer.parseInt(params.get("page")) * 10 - 10 : 0;

        ObjectMapper mapper = new ObjectMapper();
        VersionJSON versionJSON = new VersionJSON(offset, limit);

        header(ACCESS_CONTROL_ALLOW_ORIGIN, Application.CLIENT_URL);

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
        List<ServerEntity> servers = new LinkedList<>();

        VersionJSON(int offset, int limit) {
            try {
                servers = ServerEntity.getAllScope(offset, limit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Route(PATH = "/api/update.json", METHOD = "POST", TYPE = "application/json; charset=UTF-8")
    public String updateMasterServer() {
        String token = data.containsKey("token") ? data.get("token") : null;

        if (token != null && Boolean.valueOf(Application.serverConfig.get("master_server_local"))) {
            try {
                List<ServerEntity> servers = ServerEntity.getByToken(token);
                if (servers.size() > 0) {
                    ServerEntity.update(servers.get(0));
                    return "{status:good}";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return "{status:fail}";
    }
}
