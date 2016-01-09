package com.fsggs.server.core.network;

public interface INetworkPackage {
    public INetworkPackage receive();
    public INetworkPackage send(String message);
}
