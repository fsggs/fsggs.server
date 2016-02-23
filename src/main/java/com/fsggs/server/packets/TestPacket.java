package com.fsggs.server.packets;

import co.nstant.in.cbor.CborBuilder;
import co.nstant.in.cbor.CborEncoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseNetworkPacket;
import com.fsggs.server.core.network.INetworkPacket;
import com.fsggs.server.core.network.NetworkPacket;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayOutputStream;
import java.util.Objects;


@NetworkPacket(name = "Test")
public class TestPacket extends BaseNetworkPacket {

    UnicodeString message;

    public TestPacket(ChannelHandlerContext context, Map data) {
        super(context, data);
    }

    @Override
    public INetworkPacket receive() {
        Application.logger.info("Receive: TestPacket");

        message = (UnicodeString) data.get(new UnicodeString("testMessage"));

        if (message != null && Objects.equals(message.toString(), "Hello Server")) {
            send("Hello Client");
        }
        return this;
    }

    @Override
    public INetworkPacket send(String message) {
        ByteArrayOutputStream packet = new ByteArrayOutputStream();

        try {
            new CborEncoder(packet).encode(new CborBuilder()
                    .addMap()
                    .put(new UnicodeString("testMessage"), new UnicodeString(message))
                    .put(new UnicodeString("package"), new UnicodeString("Test"))
                    .end()
                    .build());

            sendBuffer(packet);
            broadcastBuffer(packet);

            Application.logger.info("Send: TestPacket");
        } catch (CborException e) {
            e.printStackTrace();
        }
        return this;
    }
}
