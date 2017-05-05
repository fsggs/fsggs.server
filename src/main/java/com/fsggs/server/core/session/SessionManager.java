package com.fsggs.server.core.session;

import com.fsggs.server.Application;
import com.fsggs.server.models.auth.UserEntity;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class SessionManager {
    static private Map<ChannelId, Session> channelSessions = new HashMap<>();
    static private Map<String, Session> sessions = new HashMap<>();

    static public void add(Channel channel) {
        final Session s = new Session(channel.id());
        channelSessions.put(channel.id(), s);
        Application.logger.log(Application.FSGGS, "Client [id: 0x" + channel.id().toString() + "] connect from "
                + channel.remoteAddress().toString());
    }

    static public boolean update(Channel channel, String session, String login) {
        if (channelSessions.containsKey(channel.id())) {
            final Session s = new Session(login, session, channel.id());
            channelSessions.put(channel.id(), s);
            sessions.put(session, s);
            if (!s.getUserIdentity().isGuest()) {
                Application.logger.log(Application.FSGGS, "Client [id: 0x" + channel.id() + "] login as "
                        + s.getUserIdentity().getUser().getLogin() + "(id:" + s.getUserIdentity().getUserId()
                        + ", acs:" + s.getUserIdentity().getAuthLevel().getLevel() + ")");
            }
            return true;
        }
        return false;
    }

    static public int getCurrentOnline() {
        return sessions.size();
    }

    static public Session getSessionByChannel(Channel channel) {
        if (channelSessions.containsKey(channel.id())) {
            return channelSessions.get(channel.id());
        }
        return null;
    }

    static public Session getSessionBySessionId(String session) {
        if (sessions.containsKey(session)) {
            return sessions.get(session);
        }
        return null;
    }

    static public void logout(Channel channel) {
        if (channelSessions.containsKey(channel.id())) {
            if (!channelSessions.get(channel.id()).getUserIdentity().isGuest()) {
                Session.UserIdentity ui = channelSessions.get(channel.id()).getUserIdentity();
                logoutFromDB(channel);
                Application.logger.log(Application.FSGGS, "Client ["
                        + ui.getUser().getLogin() + "(id:" + ui.getUserId()
                        + ", acs:" + ui.getAuthLevel().getLevel() + ")] exit");
                sessions.remove(channelSessions.get(channel.id()).getSession());
                ui = null;
            }
            Session s = new Session(channel.id());
            channelSessions.put(channel.id(), s);
        }
    }

    static public void remove(Channel channel) {
        if (getSessionByChannel(channel) != null) {
            if (!channelSessions.get(channel.id()).getUserIdentity().isGuest()) {
                Session.UserIdentity ui = channelSessions.get(channel.id()).getUserIdentity();
                // logoutFromDB(channel);
                Application.logger.log(Application.FSGGS, "Client ["
                        + ui.getUser().getLogin() + "(id:" + ui.getUserId()
                        + ", acs:" + ui.getAuthLevel().getLevel() + ")] auto exit");
                sessions.remove(channelSessions.get(channel.id()).getSession());
                ui = null;
            }
            channelSessions.remove(channel.id());
            Application.logger.log(Application.FSGGS, "Client [id: 0x" + channel.id().toString() + "] disconnect");
        }
    }

    static public void logoutFromDB(Channel channel) {
        try {
            UserEntity user = (UserEntity) UserEntity.getById(channelSessions.get(channel.id()).getUserIdentity().getUserId());
            user.setSession("");
            UserEntity.update(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
