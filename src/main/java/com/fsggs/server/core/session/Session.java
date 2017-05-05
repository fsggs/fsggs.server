package com.fsggs.server.core.session;

import com.fsggs.server.core.network.BaseNetworkPacket;
import com.fsggs.server.models.auth.UserEntity;
import io.netty.channel.ChannelId;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Objects;

import static com.fsggs.server.core.session.AuthLevel.*;

public class Session {

    private String session = "";
    private ChannelId id;

    private UserIdentity userIdentity;

    Session(ChannelId channelId) {
        this.id = channelId;
        this.userIdentity = new UserIdentity();
    }

    Session(String login, String session, ChannelId channelId) {
        this.id = channelId;
        this.session = session;
        try {
            UserEntity user = UserEntity.findByLogin(login);
            Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (user != null && Objects.equals(
                    BaseNetworkPacket.md5(user.getSession() + f.format(user.getLoginDate())), session)) {
                this.userIdentity = new UserIdentity(user);
            } else {
                this.userIdentity = new UserIdentity();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setId(ChannelId id) {
        this.id = id;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public ChannelId getId() {
        return id;
    }

    public UserIdentity getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(UserIdentity userIdentity) {
        this.userIdentity = userIdentity;
    }

    public class UserIdentity {
        private Long userId = Long.valueOf("0");
        private UserEntity user = null;
        private AuthLevel authLevel = GUEST;

        UserIdentity() {
        }

        UserIdentity(UserEntity user) {
            this.userId = user.getId();
            this.user = user;
            this.authLevel = AuthLevel.getValue(user.getAccess());
        }

        public boolean isGuest() {
            return user == null;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public UserEntity getUser() {
            return user;
        }

        public void setUser(UserEntity user) {
            this.user = user;
        }

        public AuthLevel getAuthLevel() {
            return authLevel;
        }

        public void setAuthLevel(AuthLevel authLevel) {
            this.authLevel = authLevel;
        }
    }
}
