package com.fsggs.server.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpStaticHandler {

    private boolean status = false;

    public HttpStaticHandler(ChannelHandlerContext context, FullHttpRequest request) {

    }

    public boolean getStatus() {
        return status;
    }
}
