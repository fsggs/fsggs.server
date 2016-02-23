package com.fsggs.server.core.network;

import co.nstant.in.cbor.model.Map;
import com.fsggs.server.server.SocketServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.io.ByteArrayOutputStream;

/**
 * Base Network Package
 */
abstract public class BaseNetworkPacket implements INetworkPacket {

    final protected ChannelHandlerContext context;
    protected Map data;

    public BaseNetworkPacket(ChannelHandlerContext context, Map data) {
        this.context = context;
        this.data = data;

        receive();
    }

    protected void sendBuffer(ByteArrayOutputStream output) {
        byte bytes[] = output.toByteArray();
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
        this.context.channel().writeAndFlush(new BinaryWebSocketFrame(buffer));
    }

    protected void broadcastBuffer(ByteArrayOutputStream output) {
        broadcastBuffer(output, false);
    }

    protected void broadcastBuffer(ByteArrayOutputStream output, Boolean me) {
        byte bytes[] = output.toByteArray();
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);

        for (Channel channel : SocketServerHandler.channels) {
            if (channel != context.channel() && !me) {
                channel.writeAndFlush(new BinaryWebSocketFrame(buffer));
            } else if (channel == context.channel() && me) {
                channel.writeAndFlush(new BinaryWebSocketFrame(buffer));
            }
        }
    }
}
