package com.fsggs.server.models.game;

import com.fsggs.server.Application;
import com.fsggs.server.core.db.BaseModel;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CharacterModel extends BaseModel implements ICharacterModel {

    @Override
    public void add(Character character) throws SQLException {
        Session session = null;
        try {
            session = Application.db.openSession();
            session.beginTransaction();
            session.save(character);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when insert character");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public void update(Character character) throws SQLException {
        Session session = null;
        try {
            session = Application.db.openSession();
            session.beginTransaction();
            session.update(character);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when update character");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public void delete(Character character) throws SQLException {
        Session session = null;
        try {
            session = Application.db.openSession();
            session.beginTransaction();
            session.delete(character);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when delete character");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public Character getById(Long id) throws SQLException {
        Session session = null;
        Character character = null;
        try {
            session = Application.db.openSession();
            character = session.get(Character.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getById()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return character;
    }

    @Override
    public List<Character> getAll() throws SQLException {
        Session session = null;
        List<Character> character = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(Character.class);

            character = listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getAll()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return character;
    }
}
