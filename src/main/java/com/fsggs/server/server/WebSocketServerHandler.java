package com.fsggs.server.server;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import com.fsggs.server.Application;
import com.fsggs.server.packets.TestPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

import java.io.ByteArrayInputStream;
import java.util.List;

public class WebSocketServerHandler {

    private SocketServerHandler handler;

    public WebSocketServerHandler(SocketServerHandler handler, ChannelHandlerContext context, WebSocketFrame msg) {
        this.handler = handler;
        handlerWebSocketFrame(context, msg);
    }

    private void handlerWebSocketFrame(ChannelHandlerContext context, WebSocketFrame frame) {
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

    private void handlerWebSocketPacket(ChannelHandlerContext context, ByteBuf buffer) {
        Application.logger.info("WebSocketFrameToByteBufDecoder decode - " + ByteBufUtil.hexDump(buffer));

        int numReadBytes = buffer.readableBytes();
        byte[] bytes = new byte[numReadBytes];
        buffer.getBytes(buffer.readerIndex(), bytes);

        ByteArrayInputStream data = new ByteArrayInputStream(bytes);

        List<DataItem> dataItems;
        try {
            dataItems = new CborDecoder(data).decode();

            Map map = (Map) dataItems.get(0);
            // TODO:: 
            new TestPacket(context, map);
        } catch (CborException e) {
            e.printStackTrace();
        }
    }

    private void handlerWebSocketMessage(ChannelHandlerContext context, String message) {
        String userMessage = message.substring(message.indexOf(':') + 1).trim();

        Application.logger.info("Message: " + ": " + userMessage);

        broadcast(context, userMessage);
        context.channel().writeAndFlush(new TextWebSocketFrame("[Me] " + userMessage));
    }

    private void broadcast(ChannelHandlerContext context, String message) {
        SocketServerHandler.channels.stream().filter(channel -> channel != context.channel()).forEach(
                channel -> channel.writeAndFlush(new TextWebSocketFrame(message))
        );
    }
}
