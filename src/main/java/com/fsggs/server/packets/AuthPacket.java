package com.fsggs.server.packets;

import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseNetworkPacket;
import com.fsggs.server.core.network.INetworkPacket;
import com.fsggs.server.core.network.NetworkPacket;
import com.fsggs.server.core.network.NetworkPacketParam;
import com.fsggs.server.packets.services.AuthPacketService;
import com.fsggs.server.packets.services.AuthPacketService.APSResponse;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;

import static com.fsggs.server.packets.services.AuthPacketService.APSError.E_UNKNOWN_ACTION;

@NetworkPacket("AuthPacket")
public class AuthPacket extends BaseNetworkPacket {

    static public boolean AUTO_LOGIN = true;

    private AuthPacketService APS = new AuthPacketService(this);

    private int action = 0;

    private long id = 0;

    private String login;

    private String password = "";

    private String token = "";

    protected APSResponse response = new APSResponse(this.getPacket(), this.getAction());

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
                response = APS.tryAuthWithLogin(login, password);
                break;
            case 2: // Server packet: register
                response = APS.tryRegister(login, password);
                break;

            case 3: // Server packet: remember password
                response = APS.tryRememberPassword(login, token, password);
                break;
            case 4: // Server packet: change password
                response = APS.tryChangePassword(password, token);
                break;

            case 5: // Server packet: Activate by token
                response = APS.tryActivateByToken(login, token);
                break;

            case 6: // Server packet: Activate by token
                response = APS.tryReconnect(login, session);
                break;

            case 0: // Server packet: logout
            default:
                response = APS.tryLogout();
        }

        if (action < 0 && action > 6) {
            response.addError(E_UNKNOWN_ACTION);
        }
        send();
        return this;
    }

    @Override
    public INetworkPacket send() {
        sendPacket(response);
        Application.logger.info("Send: " + packet);
        return this;
    }

    public int getAction() {
        return action;
    }

    @NetworkPacketParam("action")
    public void setAction(int action) {
        this.action = action;
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
}
