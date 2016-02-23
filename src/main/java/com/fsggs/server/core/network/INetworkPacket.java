package com.fsggs.server.core.network;

public interface INetworkPacket {
    public INetworkPacket receive();
    public INetworkPacket send(String message);
}
