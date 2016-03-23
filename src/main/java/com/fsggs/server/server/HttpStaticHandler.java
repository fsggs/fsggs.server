package com.fsggs.server.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

//TODO
public class HttpStaticHandler {

    private boolean status = false;

    public HttpStaticHandler(ChannelHandlerContext context, FullHttpRequest request) {

    }

    public boolean getStatus() {
        return this.status;
    }
}
