package com.fsggs.server.core.db;

import com.fsggs.server.models.auth.IUserModel;
import com.fsggs.server.models.auth.UserModel;
import com.fsggs.server.models.game.CharacterModel;
import com.fsggs.server.models.game.ICharacterModel;
import com.fsggs.server.models.master.IServerModel;
import com.fsggs.server.models.master.ServerModel;

public class DAOFactory {
    private static IServerModel serverDAO = null;
    private static IUserModel userDAO = null;
    private static ICharacterModel characterDAO = null;

    private static DAOFactory instance = null;

    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    public IServerModel getServer() {
        if (serverDAO == null) {
            serverDAO = new ServerModel();
        }
        return serverDAO;
    }

    public IUserModel getUser() {
        if (userDAO == null) {
            userDAO = new UserModel();
        }
        return userDAO;
    }

    public ICharacterModel getCharacter() {
        if (characterDAO == null) {
            characterDAO = new CharacterModel();
        }
        return characterDAO;
    }
}
