package com.fsggs.server.packets;

import co.nstant.in.cbor.CborBuilder;
import co.nstant.in.cbor.CborEncoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseNetworkPackage;
import com.fsggs.server.core.network.INetworkPackage;
import com.fsggs.server.core.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.io.ByteArrayOutputStream;
import java.util.Objects;


@NetworkPackage(name = "Test")
public class TestPacket extends BaseNetworkPackage {

    UnicodeString message;

    public TestPacket(ChannelHandlerContext context, Map data) {
        super(context, data);
    }

    @Override
    public INetworkPackage receive() {
        message = (UnicodeString) data.get(new UnicodeString("testMessage"));

        if (message != null && Objects.equals(message.toString(), "Hello Server")) {
            send("Hello Client");
        }

        return this;
    }

    @Override
    public INetworkPackage send(String message) {
        ByteArrayOutputStream temp = new ByteArrayOutputStream();

        try {
            new CborEncoder(temp).encode(new CborBuilder()
                    .addMap()
                    .put(new UnicodeString("testMessage"), new UnicodeString(message))
                    .put(new UnicodeString("package"), new UnicodeString("Test"))
                    .end()
                    .build());

            byte bytes[] = temp.toByteArray();

            ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
            Application.logger.info("WebSocketFrameToByteBufDecoder encode - " + ByteBufUtil.hexDump(buffer));

            context.channel().writeAndFlush(new BinaryWebSocketFrame(buffer));
        } catch (CborException e) {
            e.printStackTrace();
        }

        return this;
    }
}
