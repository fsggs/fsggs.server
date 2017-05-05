package com.fsggs.server.models.auth;

import com.fsggs.server.Application;
import com.fsggs.server.core.db.BaseModelEntity;
import com.fsggs.server.core.db.IBaseModelEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

abstract class UserModel extends BaseModelEntity {
    static public List<UserEntity> getByLogin(String login) throws SQLException {
        Session session = null;
        List<UserEntity> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(UserEntity.class)
                    .add(Restrictions.eq("login", login));

            users = UserModel.listAndCast(criteria);
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

    static public List<UserEntity> getByStatus(int status) throws SQLException {
        Session session = null;
        List<UserEntity> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(UserEntity.class)
                    .add(Restrictions.eq("status", status));

            users = UserModel.listAndCast(criteria);
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

    static public List<UserEntity> getByAccess(int access) throws SQLException {
        Session session = null;
        List<UserEntity> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(UserEntity.class)
                    .add(Restrictions.eq("access", access));

            users = UserModel.listAndCast(criteria);
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

    static public List<UserEntity> getBySession(String uSession) throws SQLException {
        Session session = null;
        List<UserEntity> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(UserEntity.class)
                    .add(Restrictions.eq("session", uSession));

            users = UserModel.listAndCast(criteria);
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

    static public List<UserEntity> getByToken(String token) throws SQLException {
        Session session = null;
        List<UserEntity> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(UserEntity.class)
                    .add(Restrictions.eq("token", token));

            users = UserModel.listAndCast(criteria);
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
    static public UserEntity findByLoginWithCharacters(String login) throws SQLException {
        Session session = null;
        List<UserEntity> users;
        try {
            session = Application.db.openSession();
            session.enableFetchProfile("user-with-characters");
            Criteria criteria = session.createCriteria(UserEntity.class)
                    .add(Restrictions.eq("login", login))
                    .add(Restrictions.ne("status", 0));
            criteria.setMaxResults(1);

            users = UserModel.listAndCast(criteria);
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

    static public UserEntity findByLogin(String login) throws SQLException {
        Session session = null;
        List<UserEntity> users;
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(UserEntity.class)
                    .add(Restrictions.eq("login", login));
            criteria.setMaxResults(1);

            users = UserModel.listAndCast(criteria);
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

    static public UserEntity findByLoginWithToken(String login, String token) throws SQLException {
        return findByLoginWithToken(login, token, 0);
    }

    static public UserEntity findByLoginWithToken(String login, String token, int status) throws SQLException {
        Session session = null;
        List<UserEntity> users;
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(UserEntity.class)
                    .add(Restrictions.eq("login", login))
                    .add(Restrictions.eq("token", token))
                    .add(Restrictions.eq("status", status));
            criteria.setMaxResults(1);

            users = UserModel.listAndCast(criteria);
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
