package com.fsggs.server.core.network;

import java.util.Map;

public interface INetworkPacket {
    INetworkPacket updateIdentity();

    INetworkPacket updateIdentity(String session);

    INetworkPacket setData(Map<?, ?> data);

    INetworkPacket receive();

    INetworkPacket send();
}
