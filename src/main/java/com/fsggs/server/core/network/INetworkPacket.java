package com.fsggs.server.core.network;

import java.util.Map;

public interface INetworkPacket {
    public INetworkPacket setData(Map<?, ?> data);

    public INetworkPacket receive();

    public INetworkPacket send();
}
