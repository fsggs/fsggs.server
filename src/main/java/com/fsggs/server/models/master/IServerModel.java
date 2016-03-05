package com.fsggs.server.models.master;

import com.fsggs.server.entities.master.Server;

import java.sql.SQLException;
import java.util.List;

public interface IServerModel {
    void add(Server server) throws SQLException;

    void update(Server server) throws SQLException;

    void delete(Server server) throws SQLException;

    Server getById(Long id) throws SQLException;

    List<Server> getAll() throws SQLException;

    List<Server> getByName(String name) throws SQLException;

    List<Server> getByAddress(String address) throws SQLException;

    List<Server> getByToken(String token) throws SQLException;

    /* Customs */

    List<Server> getAllScope(int offset, int limit) throws SQLException;
}
