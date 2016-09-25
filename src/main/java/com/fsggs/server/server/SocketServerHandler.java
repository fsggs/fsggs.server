package com.fsggs.server.server;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.lang.reflect.InvocationTargetException;

public class SocketServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    static final public ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    WebSocketServerHandshaker handshaker;

    @Override
    public void channelRead0(ChannelHandlerContext context, HttpObject message)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (message instanceof HttpRequest) {
            new HttpServerHandler(this, context, (HttpRequest) message);
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
        context.channel().close();
        context.close();
    }
}
