package com.fsggs.server.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.Application;
import com.fsggs.server.core.network.*;
import com.fsggs.server.core.session.NetworkPacketAuthLevel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;

import static com.fsggs.server.core.session.AuthLevel.GUEST;

@NetworkPacket
@NetworkPacketId(0)
@NetworkPacketAuthLevel(GUEST)
public class SimpleTestPacket extends BaseNetworkPacket {

    @JsonProperty("testMessage")
    private String message;

    public SimpleTestPacket(ChannelHandlerContext context) {
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
        sendPacket(new String[]{String.valueOf(this.packetId), message});
        Application.logger.info("Send: " + packet + ": " + message);
        return this;
    }

    @NetworkPacketParam("2")
    public INetworkPacket setMessage(String message) {
        this.message = message;
        return this;
    }
}
