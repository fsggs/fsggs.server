package com.fsggs.server.server;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import com.fsggs.server.Application;
import com.fsggs.server.core.network.INetworkPacket;
import com.fsggs.server.packets.TestPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class WebSocketServerHandler {

    private SocketServerHandler handler;

    public WebSocketServerHandler(SocketServerHandler handler, ChannelHandlerContext context, WebSocketFrame msg) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        this.handler = handler;
        handlerWebSocketFrame(context, msg);
    }

    private void handlerWebSocketFrame(ChannelHandlerContext context, WebSocketFrame frame) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handler.handshaker.close(context.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            context.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            handlerWebSocketPacket(context, frame.content());
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(
                    String.format("%s frame types not supported", frame.getClass().getName())
            );
        }

        handlerWebSocketMessage(context, ((TextWebSocketFrame) frame).text());
    }

    private void handlerWebSocketPacket(ChannelHandlerContext context, ByteBuf buffer) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        //Application.logger.info("WebSocketFrameToByteBufDecoder decode - " + ByteBufUtil.hexDump(buffer));

        int numReadBytes = buffer.readableBytes();
        byte[] bytes = new byte[numReadBytes];
        buffer.getBytes(buffer.readerIndex(), bytes);

        ByteArrayInputStream encodedData = new ByteArrayInputStream(bytes);

        List<DataItem> dataItems;
        try {
            dataItems = new CborDecoder(encodedData).decode();
            Map data = (Map) dataItems.get(0);
            String packetName = (data.get(new UnicodeString("packet"))).toString();

            if (Application.networkPackets.containsKey(packetName)) {
                try {
                    Class<?> packetClass = Application.networkPackets.get(packetName);
                    packetClass.getConstructor(ChannelHandlerContext.class, Map.class).newInstance(context, data);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {
                Application.logger.warn("Receive unregistered packet: \"" + packetName + "Packet\"!");
            }
        } catch (CborException e) {
            e.printStackTrace();
            Application.logger.warn("Receive unknown packet!");
        }
    }

    private void handlerWebSocketMessage(ChannelHandlerContext context, String message) {
        String userMessage = message.substring(message.indexOf(':') + 1).trim();

        //Application.logger.info("Message: " + ": " + userMessage);

        broadcast(context, userMessage);
        context.channel().writeAndFlush(new TextWebSocketFrame("[Me] " + userMessage));
    }

    public static void broadcast(ChannelHandlerContext context, String message) {
        for (Channel channel : SocketServerHandler.channels) {
            if (channel != context.channel()) {
                channel.writeAndFlush(new TextWebSocketFrame(message));
            }
        }
    }
}
