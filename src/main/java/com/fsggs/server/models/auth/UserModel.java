package com.fsggs.server.models.auth;

import com.fsggs.server.Application;
import com.fsggs.server.core.db.BaseEntity;
import com.fsggs.server.core.db.IBaseEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

abstract class UserModel extends BaseEntity {
    static public List<User> getByLogin(String login) throws SQLException {
        Session session = null;
        List<User> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("login", login));

            users = IBaseEntity.listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getByName()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return users;
    }

    static public List<User> getByStatus(int status) throws SQLException {
        Session session = null;
        List<User> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("status", status));

            users = IBaseEntity.listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getByName()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return users;
    }

    static public List<User> getByAccess(int access) throws SQLException {
        Session session = null;
        List<User> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("access", access));

            users = IBaseEntity.listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getByName()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return users;
    }

    static public List<User> getBySession(String uSession) throws SQLException {
        Session session = null;
        List<User> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("session", uSession));

            users = IBaseEntity.listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getByName()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return users;
    }

    static public List<User> getByToken(String token) throws SQLException {
        Session session = null;
        List<User> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("token", token));

            users = IBaseEntity.listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getByName()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return users;
    }

    /* Customs */
    static public User findByLoginWithCharacters(String login) throws SQLException {
        Session session = null;
        List<User> users;
        try {
            session = Application.db.openSession();
            session.enableFetchProfile("user-with-characters");
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("login", login))
                    .add(Restrictions.ne("status", 0));
            criteria.setMaxResults(1);

            users = IBaseEntity.listAndCast(criteria);
            if (users.size() > 0) return users.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when findByLoginWithCharacters()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }

    static public User findByLogin(String login) throws SQLException {
        Session session = null;
        List<User> users;
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("login", login));
            criteria.setMaxResults(1);

            users = IBaseEntity.listAndCast(criteria);
            if (users.size() > 0) return users.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when findByLoginWithCharacters()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }

    static public User findByLoginWithToken(String login, String token) throws SQLException {
        return findByLoginWithToken(login, token, 0);
    }

    static public User findByLoginWithToken(String login, String token, int status) throws SQLException {
        Session session = null;
        List<User> users;
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("login", login))
                    .add(Restrictions.eq("token", token))
                    .add(Restrictions.eq("status", status));
            criteria.setMaxResults(1);

            users = IBaseEntity.listAndCast(criteria);
            if (users.size() > 0) return users.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when findByLoginWithCharacters()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }
}
