package com.fsggs.server.core.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    static private Map<ChannelId, Session> sessions = new HashMap<>();

    static public void add(Channel chanel) {
        sessions.put(chanel.id(), new Session());
    }

    static public void remove(Channel chanel) {
        sessions.remove(chanel.id());
    }
}
