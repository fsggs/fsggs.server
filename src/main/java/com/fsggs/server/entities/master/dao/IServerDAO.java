package com.fsggs.server.entities.master.dao;

import com.fsggs.server.entities.master.Server;

import java.sql.SQLException;
import java.util.List;

public interface IServerDAO {
    void addServer(Server server) throws SQLException;

    void updateServer(Server server) throws SQLException;

    void deleteServer(Server server) throws SQLException;

    Server getServerById(Long id) throws SQLException;

    List<Server> getAllServers() throws SQLException;

    List<Server> getAllServersScope(int offset, int limit) throws SQLException;

    List<Server> getServersByName(String name) throws SQLException;

    List<Server> getServersByAddress(String address) throws SQLException;
}
