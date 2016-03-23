package com.fsggs.server.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseNetworkPacket;
import com.fsggs.server.core.network.INetworkPacket;
import com.fsggs.server.core.network.NetworkPacket;
import com.fsggs.server.core.network.NetworkPacketParam;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

@NetworkPacket("PingPacket")
public class PingPacket extends BaseNetworkPacket {
    public final int COUNT_OF_PING = 10;

    @JsonProperty("startTime")
    private long startTime = 0;

    @JsonProperty("deltaTime")
    private long deltaTime = 0;

    @JsonProperty("count")
    private int count = 0;

    public PingPacket(ChannelHandlerContext context) {
        super(context);
    }

    public PingPacket(ChannelHandlerContext context, Map<?, ?> data) {
        super(context, data);
    }

    @Override
    public INetworkPacket receive() {
        if (count < COUNT_OF_PING) {
            if (startTime != 0) deltaTime = (deltaTime + (startTime - System.currentTimeMillis())) / 2;
            count++;
            send();
        } else {
            count = 0;
            startTime = 0;
            deltaTime = 0;
        }
        return this;
    }

    @Override
    public INetworkPacket send() {
        startTime = System.currentTimeMillis();
        sendPacket();
        return this;
    }

    @NetworkPacketParam("startTime")
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @NetworkPacketParam("deltaTime")
    public void setDeltaTime(long deltaTime) {
        this.deltaTime = deltaTime;
    }

    @NetworkPacketParam("count")
    public void setCount(int count) {
        this.count = count;
    }
}
