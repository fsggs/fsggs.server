package com.fsggs.server.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseNetworkPacket;
import com.fsggs.server.core.network.INetworkPacket;
import com.fsggs.server.core.network.NetworkPacket;
import com.fsggs.server.core.network.NetworkPacketParam;
import com.fsggs.server.entities.auth.User;
import com.fsggs.server.entities.game.Character;
import io.netty.channel.ChannelHandlerContext;
import java.util.*;

@NetworkPacket("AuthPacket")
public class AuthPacket extends BaseNetworkPacket {

    @JsonProperty("action")
    private int action = 0;

    @JsonProperty("errors")
    List<String> errors = new ArrayList<String>();

    @JsonProperty("id")
    private long id = 0;

    @JsonProperty("login")
    private String login;

    @JsonProperty("password")
    private String password = "";

    @JsonProperty("token")
    private String token = "";

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
        switch (action) {
            case 1: // Server packet: login with password
                checkAuthWithLogin();
                break;
            case 2: // Server packet: register
                break;

            case 3: // Server packet: remember password
                break;
            case 4: // Server packet: change password
                break;

            case 5: // Server packet: get characters list
                break;

            case 0: // Server packet: logout
            default:
        }

        if (action >= 0 && action <= 5) send();
        return this;
    }

    private void checkAuthWithLogin() {
        if (!Objects.equals(login, "")) {
            try {
                User user = Application.dao.getUser().findByLoginWithCharacters(login);
                if (user != null) {
                    if (Objects.equals(user.getPassword(), password)) {
                        user.setSession(md5(login + String.valueOf(System.currentTimeMillis())));
                        Application.dao.getUser().update(user);

                        password = "";
                        token = "";
                        id = user.getId();
                        session = user.getSession();
                        characters = user.getCharacters();
                    } else errors.add("Password mismatch");
                } else errors.add("User not found");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else errors.add("Login must be not clear");
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
    public void setTokken(String token) {
        this.token = token;
    }
}
