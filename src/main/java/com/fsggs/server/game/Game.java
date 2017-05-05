package com.fsggs.server.game;

import com.fsggs.server.Application;
import com.fsggs.server.game.maps.SpaceMap;

import static com.fsggs.server.Application.APPLICATION_VERSION_ID;
import static com.fsggs.server.Application.FSGGS;

public class Game {
    static private final String UPDATE = "UP0";

    private SpaceMap map = new SpaceMap();

    static private Game instance;

    private Game() {
        if (instance == null) instance = this;
    }

    static public void initialize() {
        (new Game()).init();
        instance.load();
    }

    static public Game getInstance() {
        return instance;
    }

    private void init() {
        map.loadChunk(1, 1, 1);
    }

    private void load() {
        Application.logger.log(FSGGS, "Update " + '"' + UPDATE + ' ' + APPLICATION_VERSION_ID + "\" loaded.");
    }

    public void halt() {
        map.unLoadChunk(1, 1, 1);
    }

    public SpaceMap getMap() {
        return map;
    }
}
