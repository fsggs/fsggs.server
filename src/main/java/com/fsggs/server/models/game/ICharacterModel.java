package com.fsggs.server.models.game;

import java.sql.SQLException;
import java.util.List;

public interface ICharacterModel {
    void add(Character character) throws SQLException;

    void update(Character character) throws SQLException;

    void delete(Character character) throws SQLException;

    Character getById(Long id) throws SQLException;

    List<Character> getAll() throws SQLException;
}
