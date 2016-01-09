package com.fsggs.server.core.network;

import co.nstant.in.cbor.model.Map;
import io.netty.channel.ChannelHandlerContext;

/**
 * Base Network Package
 */
abstract public class BaseNetworkPackage implements INetworkPackage {

    final protected ChannelHandlerContext context;
    protected Map data;

    public BaseNetworkPackage(ChannelHandlerContext context, Map data) {
        this.context = context;
        this.data = data;

        receive();
    }
}
