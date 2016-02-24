package com.fsggs.server.core.network;

import co.nstant.in.cbor.model.Map;

public interface INetworkPacket {
    public INetworkPacket setData(Map data);
    public INetworkPacket receive();
    public INetworkPacket send(String message);
}
