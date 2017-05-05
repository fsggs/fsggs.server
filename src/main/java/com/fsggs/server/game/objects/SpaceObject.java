package com.fsggs.server.game.objects;

import com.fsggs.server.game.maps.SpacePosition;
import com.fsggs.server.models.game.objects.SpaceObjectEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpaceObject {
    private SpacePosition position;
    private SpaceObjectType type = SpaceObjectType.UNKNOWN_ANOMALY;
    private SpaceObjectEntity entity;

    public SpaceObject(SpaceObjectType type, SpacePosition position) {
        this.type = type;
        try {
            entity = new SpaceObjectEntity(type.getTypeId());
            setPosition(position);
            entity.setName(getName());
            entity.save();
            save();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SpaceObject(int type, SpacePosition position) {
        this(SpaceObjectType.getType(type), position);
    }

    public SpaceObject(SpaceObjectEntity object) {
        entity = object;
        getPosition();
        type = SpaceObjectType.getType(entity.getTypeId());
    }

    private static SpaceObject fromEntity(SpaceObjectEntity object) {
        return new SpaceObject(object);
    }

    public String getName() {
        return type.getIndex() + '-'
                + 'U' + position.getUniverse()
                + 'G' + position.getGalaxy()
                + 'S' + position.getSolar()
                + 'I' + (entity.getId() != null ? entity.getId() : "N");
    }

    public void save() {
        try {
            entity.setName(getName());
            entity.save();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getType() {
        return type.getType() + '.' + type.getMetaType();
    }

    public SpacePosition getPosition() {
        if (position == null) {
            position = new SpacePosition(
                    entity.getUniverseId(),
                    entity.getGalaxyId(),
                    entity.getSolarId(),
                    entity.getPosX(),
                    entity.getPosY(),
                    entity.getPosZ()
            );
        }
        return position;
    }

    public SpaceObject setPosition(SpacePosition position) {
        return setPosition(position, false);
    }

    public SpaceObject setPosition(SpacePosition position, boolean save) {
        try {
            this.position = position;
            entity.setUniverseId(position.getUniverse());
            entity.setGalaxyId(position.getGalaxy());
            entity.setSolarId(position.getSolar());
            entity.setPosX(position.getPosX());
            entity.setPosY(position.getPosY());
            entity.setPosZ(position.getPosZ());
            if (save) entity.save();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + getType() + '.' + ":" + getPosition() + "}";
    }

    static public List<SpaceObject> loadSolarChunk(long universeId, long galaxyId, long solarId) {
        List<SpaceObject> list = new ArrayList<>();
        try {
            List<SpaceObjectEntity> objects = SpaceObjectEntity.getSolarChunk(universeId, galaxyId, solarId);
            list.addAll(objects.stream().map(SpaceObject::fromEntity).collect(Collectors.toList()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
