package com.fsggs.server.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseNetworkPacket;
import com.fsggs.server.core.network.INetworkPacket;
import com.fsggs.server.core.network.NetworkPacket;
import com.fsggs.server.core.network.NetworkPacketParam;
import com.fsggs.server.core.session.NetworkPacketAuthLevel;
import io.netty.channel.ChannelHandlerContext;

import static com.fsggs.server.core.session.AuthLevel.*;

import java.util.Map;
import java.util.Objects;

@NetworkPacket("TestPacket")
@NetworkPacketAuthLevel(GUEST)
public class TestPacket extends BaseNetworkPacket {

    @JsonProperty("testMessage")
    private String message;

    public TestPacket(ChannelHandlerContext context, Map data) {
        super(context, data);
    }

    public TestPacket(ChannelHandlerContext context) {
        super(context);
    }

    @Override
    public INetworkPacket receive() {
        Application.logger.info("Receive: " + packet + ": " + message);

        if (message != null && Objects.equals(message, "Hello Server")) {
            message = "Hello Client";
            send();
        }
        return this;
    }

    @Override
    public INetworkPacket send() {

        sendPacket();
        broadcastPacket();

        Application.logger.info("Send: " + packet + ": " + message);
        return this;
    }

    @NetworkPacketParam("testMessage")
    public INetworkPacket setMessage(String message) {
        this.message = message;
        return this;
    }
}
