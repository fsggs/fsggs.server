package com.fsggs.server.game.maps;

import com.fsggs.server.Application;
import com.fsggs.server.game.objects.GameObject;

import java.util.*;

import static com.fsggs.server.Application.FSGGS;

public class SpaceMap {
    private Map<Long, Map<Long, Map<Long, List<GameObject>>>> map = new HashMap<>();

    public boolean loadChunk(long universe, long galaxy, long solar) {
        if (!map.containsKey(universe)) map.put(universe, new HashMap<>());
        if (!map.get(universe).containsKey(galaxy)) map.get(universe).put(galaxy, new HashMap<>());
        if (!map.get(universe).get(galaxy).containsKey(solar))
            map.get(universe).get(galaxy).put(solar, GameObject.loadSolarChunk(universe, galaxy, solar));

        Application.logger.log(FSGGS, "Map chunk [" + universe + ":" + galaxy + ":" + solar + "] loaded.");
        return true;
    }

    public boolean saveChunk(long universe, long galaxy, long solar) {
        return isChunkLoaded(universe, galaxy, solar);
    }

    public boolean unLoadChunk(long universe, long galaxy, long solar) {
        if (saveChunk(universe, galaxy, solar)) {
            map.get(universe).get(galaxy).remove(solar);
            Application.logger.log(FSGGS, "Map chunk [" + universe + ":" + galaxy + ":" + solar + "] unloaded.");
        } else return false;
        return true;
    }

    public List<GameObject> getChunk(long universe, long galaxy, long solar) {
        if (isChunkLoaded(universe, galaxy, solar)) {
            return map.get(universe).get(galaxy).get(solar);
        }
        return null;
    }

    public SpaceMap addObject(GameObject object) {
        SpacePosition position = object.getPosition();

        List<GameObject> local = getChunk(position.getUniverse(), position.getGalaxy(), position.getSolar());

        if (local != null) {
            local.add(object);
        } else {
            Application.logger.warn("Trying add " + object + " to unloaded chunk.");
        }
        return this;
    }

    public boolean isChunkLoaded(long universe, long galaxy, long solar) {
        return map.containsKey(universe)
                && map.get(universe).containsKey(galaxy)
                && map.get(universe).get(galaxy).containsKey(solar);
    }
}
