package com.fsggs.server.core.session;

public enum AuthLevel {
    GUEST(0), PLAYER(1), GM(2), ADMIN(3);

    private int level;

    AuthLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
