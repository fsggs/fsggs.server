package com.fsggs.server.game.maps;

public class SpacePosition {
    private long universe = 0;
    private long galaxy = 0;
    private long solar = 0;
    private long posX = 0;
    private long posY = 0;
    private long posZ = 0;

    public SpacePosition(long universe, long galaxy, long solar, long posX, long posY, long posZ) {
        this.universe = universe;
        this.galaxy = galaxy;
        this.solar = solar;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public SpacePosition(long posX, long posY, long posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public void setPosition(long universe, long galaxy, long solar, long posX, long posY, long posZ) {
        this.universe = universe;
        this.galaxy = galaxy;
        this.solar = solar;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public void setPosition(long posX, long posY, long posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public long getUniverse() {
        return universe;
    }

    public long getGalaxy() {
        return galaxy;
    }

    public long getSolar() {
        return solar;
    }

    public long getPosX() {
        return posX;
    }

    public long getPosY() {
        return posY;
    }

    public long getPosZ() {
        return posZ;
    }

    @Override
    public String toString() {
        return "[" + universe + ":" + galaxy + ":" + solar + ":" + posX + ":" + posY + ":" + posZ + ']';
    }
}
