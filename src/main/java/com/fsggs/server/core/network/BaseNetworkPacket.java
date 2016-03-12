package com.fsggs.server.core.network;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fsggs.server.server.SocketServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;

/**
 * Base Network Package
 */
abstract public class BaseNetworkPacket implements INetworkPacket {

    final protected ChannelHandlerContext context;
    final private CBORFactory cborFactory = new CBORFactory();

    @JsonProperty
    protected String packet = "UnknownPacket";

    @JsonProperty
    protected String session = "";

    protected Map<?, ?> data;

    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public BaseNetworkPacket(ChannelHandlerContext context) {
        this.context = context;
    }

    public BaseNetworkPacket(ChannelHandlerContext context, Map<?, ?> data) {
        this.context = context;
        this.data = data;
    }

    @NetworkPacketParam("packet")
    public INetworkPacket setPacket(String packet) {
        this.packet = packet;
        return this;
    }

    @NetworkPacketParam("session")
    public INetworkPacket setSession(String session) {
        this.session = session;
        return this;
    }

    public INetworkPacket setData(Map<?, ?> data) {
        this.data = data;
        return this;
    }

    private void sendBuffer(ByteArrayOutputStream output) {
        byte bytes[] = output.toByteArray();
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
        this.context.channel().writeAndFlush(new BinaryWebSocketFrame(buffer));
    }

    private void broadcastBuffer(ByteArrayOutputStream output) {
        broadcastBuffer(output, false);
    }

    private void broadcastBuffer(ByteArrayOutputStream output, Boolean me) {
        byte bytes[] = output.toByteArray();
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);

        for (Channel channel : SocketServerHandler.channels) {
            if (channel != context.channel() && !me) {
                channel.writeAndFlush(new BinaryWebSocketFrame(buffer));
            } else if (me) {
                channel.writeAndFlush(new BinaryWebSocketFrame(buffer));
            }
        }
    }

    protected void sendPacket() {
        prepareData();
        sendBuffer(out);
    }

    protected void broadcastPacket() {
        broadcastPacket(false);
    }

    protected void broadcastPacket(Boolean me) {
        prepareData();
        broadcastBuffer(out, me);
    }

    private void prepareData() {
        if (out.size() == 0) {
            try {
                final ObjectMapper mapper = new ObjectMapper(cborFactory);
                mapper.writeValue(out, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected String md5(String string) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(string.getBytes(), 0, string.length());
        return new BigInteger(1, md5.digest()).toString(16);
    }
}
