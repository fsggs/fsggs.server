package com.fsggs.server.server;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.lang.reflect.InvocationTargetException;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

public class SocketServerHandler extends SimpleChannelInboundHandler<Object> {

    static final public ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public WebSocketServerHandshaker handshaker;

    @Override
    public void messageReceived(ChannelHandlerContext context, Object message) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (message instanceof FullHttpRequest) {
            new HttpServerHandler(this, context, (FullHttpRequest) message);
        } else if (message instanceof WebSocketFrame) {
            new WebSocketServerHandler(this, context, (WebSocketFrame) message);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }
}
