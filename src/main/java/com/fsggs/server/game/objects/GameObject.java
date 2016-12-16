package com.fsggs.server.game.objects;

import com.fsggs.server.game.maps.SpacePosition;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameObject {
    private SpacePosition position;
    private GameObjectType type = GameObjectType.UNKNOWN_ANOMALY;
    private com.fsggs.server.models.game.objects.GameObject entity;

    public GameObject(GameObjectType type, SpacePosition position) {
        this.type = type;
        try {
            entity = new com.fsggs.server.models.game.objects.GameObject(type.getTypeId());
            setPosition(position);
            entity.setName(getName());
            entity.save();
            save();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public GameObject(int type, SpacePosition position) {
        this(GameObjectType.getType(type), position);
    }

    public GameObject(com.fsggs.server.models.game.objects.GameObject object) {
        entity = object;
        getPosition();
        type = GameObjectType.getType(entity.getTypeId());
    }

    private static GameObject fromEntity(com.fsggs.server.models.game.objects.GameObject object) {
        return new GameObject(object);
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

    public GameObject setPosition(SpacePosition position) {
        return setPosition(position, false);
    }

    public GameObject setPosition(SpacePosition position, boolean save) {
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

    static public List<GameObject> loadSolarChunk(long universeId, long galaxyId, long solarId) {
        List<GameObject> list = new ArrayList<>();
        try {
            List<com.fsggs.server.models.game.objects.GameObject> objects
                    = com.fsggs.server.models.game.objects.GameObject.getSolarChunk(universeId, galaxyId, solarId);
            list.addAll(objects.stream().map(GameObject::fromEntity).collect(Collectors.toList()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
