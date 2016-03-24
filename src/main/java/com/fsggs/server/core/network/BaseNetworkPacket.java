package com.fsggs.server.core.network;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fsggs.server.core.session.Session;
import com.fsggs.server.core.session.SessionManager;
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
import java.util.Objects;

/**
 * Base Network Package
 */
abstract public class BaseNetworkPacket implements INetworkPacket {

    final protected ChannelHandlerContext context;
    final private CBORFactory cborFactory = new CBORFactory();

    @JsonIgnore
    protected Session.UserIdentity auth;

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

    public Session.UserIdentity getAuth() {
        return auth;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public INetworkPacket updateIdentity() {
        Session s = SessionManager.getSessionByChannel(context.channel());
        if (s != null) {
            auth = s.getUserIdentity();
        }
        return this;
    }

    public INetworkPacket updateIdentity(String session) {
        if (!Objects.equals(session, "")) {
            Session s = SessionManager.getSessionBySessionId(session);
            if (s != null) {
                auth = s.getUserIdentity();
                return this;
            }
        }
        return updateIdentity();
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
        prepareData(this);
        sendBuffer(out);
    }

    protected void sendPacket(Object object) {
        prepareData(object);
        sendBuffer(out);
    }

    protected void broadcastPacket() {
        broadcastPacket(false);
    }

    protected void broadcastPacket(Object object) {
        broadcastPacket(object, false);
    }

    protected void broadcastPacket(Boolean me) {
        prepareData(this);
        broadcastBuffer(out, me);
    }

    protected void broadcastPacket(Object object, Boolean me) {
        prepareData(object);
        broadcastBuffer(out, me);
    }

    private void prepareData(Object object) {
        if (out.size() == 0) {
            try {
                final ObjectMapper mapper = new ObjectMapper(cborFactory);
                mapper.writeValue(out, object);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static public String md5(String string) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(string.getBytes(), 0, string.length());
        return new BigInteger(1, md5.digest()).toString(16);
    }
}
