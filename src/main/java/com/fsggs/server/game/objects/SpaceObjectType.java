package com.fsggs.server.game.objects;

public enum SpaceObjectType {
    UNKNOWN_ANOMALY(0, "anomaly", "unknown", "AUX"),
    UNKNOWN_SOLAR(10, "solar", "unknown", "SU1"),
    UNKNOWN_PLANET(100, "planet", "unknown", "PU1");

    private long typeId;
    private String type;
    private String metaType;
    private String index;

    SpaceObjectType(long typeId, String type, String metaType, String index) {
        this.typeId = typeId;
        this.type = type;
        this.metaType = metaType;
        this.index = index;
    }

    public long getTypeId() {
        return typeId;
    }

    public String getType() {
        return type;
    }

    public String getMetaType() {
        return metaType;
    }

    public String getIndex() {
        return index;
    }

    static public SpaceObjectType getType(long typeId) {
        for (SpaceObjectType got : SpaceObjectType.values()) {
            if (got.typeId == typeId) {
                return got;
            }
        }
        return null;
    }
}
