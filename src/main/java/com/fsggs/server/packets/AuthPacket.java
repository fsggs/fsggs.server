package com.fsggs.server.packets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseNetworkPacket;
import com.fsggs.server.core.network.INetworkPacket;
import com.fsggs.server.core.network.NetworkPacket;
import com.fsggs.server.core.network.NetworkPacketParam;
import com.fsggs.server.entities.game.Character;
import com.fsggs.server.packets.services.AuthPacketService;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;

@NetworkPacket("AuthPacket")
public class AuthPacket extends BaseNetworkPacket {

    @JsonIgnore
    static public boolean AUTO_LOGIN = true;

    @JsonIgnore
    private AuthPacketService APS = new AuthPacketService(this);

    @JsonProperty("action")
    private int action = 0;

    @JsonProperty("errors")
    List<String> errors = new ArrayList<>();

    @JsonProperty("id")
    private long id = 0;

    @JsonProperty("login")
    private String login;

    @JsonProperty("password")
    private String password = "";

    @JsonProperty("token")
    private String token = "";

    @JsonProperty("result")
    private boolean result;

    @JsonProperty("characters")
    private Set<Character> characters = new LinkedHashSet<>();

    public AuthPacket(ChannelHandlerContext context) {
        super(context);
    }

    public AuthPacket(ChannelHandlerContext context, Map<?, ?> data) {
        super(context, data);
    }

    @Override
    public INetworkPacket receive() {
        Application.logger.info("Receive: " + packet);
        result = false;
        switch (action) {
            case 1: // Server packet: login with password
                APS.tryAuthWithLogin(login, password);
                break;
            case 2: // Server packet: register
                APS.tryRegister(login, password);
                break;

            case 3: // Server packet: remember password
                APS.tryRememberPassword(login, token, password);
                break;
            case 4: // Server packet: change password
                APS.tryChangePassword(password, token);
                break;

            case 5: // Server packet: Activate by token
                APS.tryActivateByToken(login, token);
                break;

            case 6: // Server packet: Activate by token
                APS.tryReconnect(login, session);
                break;

            case 0: // Server packet: logout
            default:
                APS.tryLogout();
        }

        if (action < 0 && action > 6) addError("Unknown action");
        send();
        return this;
    }

    @Override
    public INetworkPacket send() {
        sendPacket();
        Application.logger.info("Send: " + packet);
        return this;
    }

    @NetworkPacketParam("action")
    public void setAction(int action) {
        this.action = action;
    }

    @NetworkPacketParam("errors")
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    @NetworkPacketParam("id")
    public void setId(long id) {
        this.id = id;
    }

    @NetworkPacketParam("login")
    public void setLogin(String login) {
        this.login = login;
    }

    @NetworkPacketParam("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @NetworkPacketParam("token")
    public void setToken(String token) {
        this.token = token;
    }

    public void setCharacters(Set<Character> characters) {
        this.characters = characters;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void addError(String error) {
        errors.add(error);
    }
}
