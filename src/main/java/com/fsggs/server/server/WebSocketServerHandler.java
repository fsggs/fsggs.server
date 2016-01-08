package com.fsggs.server.server;

import com.fsggs.server.Application;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

public class WebSocketServerHandler {

    private SocketServerHandler handler;

    public WebSocketServerHandler(SocketServerHandler handler, ChannelHandlerContext context, WebSocketFrame msg) {
        this.handler = handler;
        handlerWebSocketFrame(context, msg);
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handler.handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(
                    String.format("%s frame types not supported", frame.getClass().getName())
            );
        }

        handleWebSocketMessage(ctx, ((TextWebSocketFrame) frame).text());
    }

    private void handleWebSocketMessage(ChannelHandlerContext context, String message) {
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
