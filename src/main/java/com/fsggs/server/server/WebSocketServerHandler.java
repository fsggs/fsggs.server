package com.fsggs.server.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fsggs.server.Application;
import com.fsggs.server.core.FrameworkRegistry;
import com.fsggs.server.core.network.INetworkPacket;
import com.fsggs.server.core.session.SessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

class WebSocketServerHandler {

    private SocketServerHandler handler;

    WebSocketServerHandler(SocketServerHandler handler, ChannelHandlerContext context, WebSocketFrame msg) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        this.handler = handler;
        handlerWebSocketFrame(context, msg);
    }

    private void handlerWebSocketFrame(ChannelHandlerContext context, WebSocketFrame frame) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            Channel channel = context.channel();
            SessionManager.remove(channel);
            handler.handshaker.close(channel, (CloseWebSocketFrame) frame.retain());
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

        handlerWebSocketPacketManager(context, encodedData);
    }

    private void handlerWebSocketPacketManager(ChannelHandlerContext context, ByteArrayInputStream encodedData)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {

        CBORFactory f = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(f);
        try {
            Map<?, ?> data = mapper.readValue(encodedData, Map.class);

            if (data.containsKey("packet")) {
                String packetName = data.get("packet").toString();

                if (Application.registry.hasPacket(packetName)) {
                    FrameworkRegistry.FrameworkPacket fPacket = Application.registry.getPacket(packetName);

                    try {
                        INetworkPacket packet = (INetworkPacket) fPacket.getClassName().getConstructor(ChannelHandlerContext.class)
                                .newInstance(context);

                        for(Map.Entry<String, Method> entry : fPacket.getParams().entrySet()){
                            if (data.containsKey(entry.getKey())) {
                                entry.getValue().invoke(packet, data.get(entry.getKey()));
                            }
                        }

                        packet.setData(data);
                        if (data.containsKey("session")) {
                            packet.updateIdentity(data.get("session").toString());
                        } else packet.updateIdentity();
                        packet.receive();

                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                } else {
                    Application.logger.warn("Receive unregistered packet: \"" + packetName + "\"!");
                }
            } else {
                Application.logger.warn("Receive unregistered CBOR data: \r\n \r\n " + encodedData.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlerWebSocketMessage(ChannelHandlerContext context, String message) {
        String userMessage = message.substring(message.indexOf(':') + 1).trim();

        //Application.logger.info("Message: " + ": " + userMessage);

        broadcast(context, userMessage);
        context.channel().writeAndFlush(new TextWebSocketFrame("[Me] " + userMessage));
    }

    private static void broadcast(ChannelHandlerContext context, String message) {
        broadcast(context, message, false);
    }

    private static void broadcast(ChannelHandlerContext context, String message, Boolean me) {
        for (Channel channel : SocketServerHandler.channels) {
            if (channel != context.channel() && !me) {
                channel.writeAndFlush(new TextWebSocketFrame(message));
            } else if (me) {
                channel.writeAndFlush(new TextWebSocketFrame(message));
            }
        }
    }
}
