package com.fsggs.server.packets;

import co.nstant.in.cbor.CborBuilder;
import co.nstant.in.cbor.CborEncoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.*;
import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseNetworkPacket;
import com.fsggs.server.core.network.INetworkPacket;
import com.fsggs.server.core.network.NetworkPacket;
import com.fsggs.server.core.network.NetworkPacketParam;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayOutputStream;
import java.util.Objects;


@NetworkPacket("Test")
public class TestPacket extends BaseNetworkPacket {

    UnicodeString message;

    public TestPacket(ChannelHandlerContext context, Map data) {
        super(context, data);
    }

    public TestPacket(ChannelHandlerContext context) {
        super(context);
    }

    @Override
    public INetworkPacket receive() {
        Application.logger.info("Receive: TestPacket: " + message);

        //message = (UnicodeString) data.get(new UnicodeString("testMessage"));

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
                    .put(new UnicodeString("package"), new DoublePrecisionFloat(2.3))
                    .end()
                    .build());

            sendBuffer(packet);
            broadcastBuffer(packet);

            Application.logger.info("Send: TestPacket: " + message);
        } catch (CborException e) {
            e.printStackTrace();
        }
        return this;
    }

    @NetworkPacketParam("testMessage")
    public INetworkPacket setMessage(UnicodeString message) {
        this.message = message;
        return this;
    }

//    @NetworkPacketParam("test1")
//    public INetworkPacket setMessage1(Map message) {
//        Application.logger.warn(message.get(new UnicodeString("testi")).toString());
//        return this;
//    }
//
//    @NetworkPacketParam("test2")
//    public INetworkPacket setMessage2(Array message) {
//        Application.logger.warn(message.toString());
//        return this;
//    }
//
//    @NetworkPacketParam("test3")
//    public INetworkPacket setMessage3(Array message) {
//        Application.logger.warn(message.toString());
//        return this;
//    }
//
//    @NetworkPacketParam("test4")
//    public INetworkPacket setMessage4(UnsignedInteger message) {
//        Application.logger.warn(message.toString());
//        return this;
//    }
//
//    @NetworkPacketParam("test5")
//    public INetworkPacket setMessage5(DoublePrecisionFloat message) {
//        Application.logger.warn(message.toString());
//        return this;
//    }
}
