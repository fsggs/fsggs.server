package com.fsggs.server.models.auth;

import com.fsggs.server.entities.auth.User;

import java.sql.SQLException;
import java.util.List;

public interface IUserModel {
    void add(User user) throws SQLException;

    void update(User user) throws SQLException;

    void delete(User user) throws SQLException;

    User getById(Long id) throws SQLException;

    List<User> getAll() throws SQLException;

    List<User> getByLogin(String login) throws SQLException;

    List<User> getByStatus(int status) throws SQLException;

    List<User> getByAccess(int access) throws SQLException;

    List<User> getBySession(String session) throws SQLException;

    List<User> getByToken(String token) throws SQLException;

    /* Customs */
    User findByLoginWithCharacters(String login) throws SQLException;

    User findByLogin(String login) throws SQLException;

    User findByLoginWithToken(String login, String token) throws SQLException;

    User findByLoginWithToken(String login, String token, int status) throws SQLException;
}
