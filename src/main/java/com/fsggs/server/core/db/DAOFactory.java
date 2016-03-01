package com.fsggs.server.core.db;

import com.fsggs.server.entities.master.dao.IServerDAO;
import com.fsggs.server.entities.master.dao.ServerDAO;

public class DAOFactory {
    private static IServerDAO serverDAO = null;

    private static DAOFactory instance = null;

    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    public IServerDAO getServerDAO() {
        if (serverDAO == null) {
            serverDAO = new ServerDAO();
        }
        return serverDAO;
    }
}
