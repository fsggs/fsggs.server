package com.fsggs.server.server;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.flywaydb.core.internal.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        String packetName;
        Map<?, ?> data;
        CBORFactory f = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(f);

        try {
            JsonNode node = mapper.readTree(encodedData);
            encodedData.reset();

            switch (node.getNodeType()) {
                case NULL:
                case BOOLEAN:
                    break;
                case ARRAY:
                    List<String> params = Arrays.asList(mapper.readValue(encodedData, String[].class));
                    data = params.stream().collect(Collectors.toMap(t -> String.valueOf(params.indexOf(t)), i -> i));

                    packetName = (String) data.get("0");
                    packetName = StringUtils.isNumeric(packetName)
                            ? FrameworkRegistry.SYSTEM_PACKET_NAME + packetName
                            : packetName;
                    this.handlerPacket(context, packetName, data);
                    break;
                case NUMBER:
                    int packetId = mapper.readValue(encodedData, Integer.class);
                    if (packetId >= 0) {
                        packetName = FrameworkRegistry.SYSTEM_PACKET_NAME + packetId;
                        this.handlerPacket(context, packetName, null);
                    }
                    Application.logger.warn("Receive packet with negative id: " + packetId + ". Not supported.");
                    break;
                case STRING:
                    packetName = mapper.readValue(encodedData, String.class);
                    this.handlerPacket(context, packetName, null);
                    break;
                case OBJECT:
                    data = mapper.readValue(encodedData, Map.class);

                    if (data.containsKey("packet")) {
                        packetName = data.get("packet").toString();
                        this.handlerPacket(context, packetName, data);
                    } else if (data.containsKey("packetId")) {
                        packetName = FrameworkRegistry.SYSTEM_PACKET_NAME + data.get("packetId").toString();
                        this.handlerPacket(context, packetName, data);
                    } else {
                        Application.logger.warn("Receive packet without identification!");
                    }
                    break;
                default:
                    Application.logger.warn("Receive unregistered CBOR " + node.getNodeType().toString() + " with data: \r\n  " + decodeToHex(encodedData));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlerPacket(ChannelHandlerContext context, String packetName, Map<?, ?> data)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (Application.registry.hasPacket(packetName)) {
            this.runPacket(context, packetName, data);
        } else {
            if (packetName.startsWith(FrameworkRegistry.SYSTEM_PACKET_NAME)) {
                packetName = packetName.substring(FrameworkRegistry.SYSTEM_PACKET_NAME.length());
                Application.logger.warn("Receive unregistered packet with id: \"" + packetName + "\"!");
            } else {
                Application.logger.warn("Receive unregistered packet: \"" + packetName + "\"!");
            }
        }
    }

    private void runPacket(ChannelHandlerContext context, String packetName, Map<?, ?> data)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        FrameworkRegistry.FrameworkPacket fPacket = Application.registry.getPacket(packetName);

        try {
            INetworkPacket packet = (INetworkPacket) fPacket.getClassName().getConstructor(ChannelHandlerContext.class)
                    .newInstance(context);

            for (Map.Entry<String, Method> entry : fPacket.getParams().entrySet()) {
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
    }

    private String decodeToHex(ByteArrayInputStream encodedData) {
        byte[] bytes = new byte[encodedData.available()];
        //noinspection ResultOfMethodCallIgnored
        encodedData.read(bytes, 0, encodedData.available());
        return javax.xml.bind.DatatypeConverter.printHexBinary(bytes);
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
