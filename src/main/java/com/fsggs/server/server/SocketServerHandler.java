package com.fsggs.server.server;

import com.fsggs.server.Application;
import com.fsggs.server.core.log.FSGGSLevel;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.lang.reflect.InvocationTargetException;

public class SocketServerHandler extends SimpleChannelInboundHandler<Object> {

    static final public ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    WebSocketServerHandshaker handshaker;

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = null;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = null;
    }

    @Override
    public void channelRead0(ChannelHandlerContext context, Object message)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {

        Application.logger.log(Application.FSGGS, message);

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
