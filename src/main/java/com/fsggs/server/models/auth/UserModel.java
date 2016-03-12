package com.fsggs.server.models.auth;

import com.fsggs.server.Application;
import com.fsggs.server.core.db.BaseModel;
import com.fsggs.server.entities.auth.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserModel extends BaseModel implements IUserModel {
    @Override
    public void add(User user) throws SQLException {
        Session session = null;
        try {
            session = Application.db.openSession();
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when insert user");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public void update(User user) throws SQLException {
        Session session = null;
        try {
            session = Application.db.openSession();
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when update user");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public void delete(User user) throws SQLException {
        Session session = null;
        try {
            session = Application.db.openSession();
            session.beginTransaction();
            session.delete(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when delete user");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public User getById(Long id) throws SQLException {
        Session session = null;
        User user = null;
        try {
            session = Application.db.openSession();
            user = session.load(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getById()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return user;
    }

    @Override
    public List<User> getAll() throws SQLException {
        Session session = null;
        List<User> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class);

            servers = listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getAll()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return servers;
    }

    @Override
    public List<User> getByLogin(String login) throws SQLException {
        Session session = null;
        List<User> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("login", login));

            users = listAndCast(criteria);
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

    @Override
    public List<User> getByStatus(int status) throws SQLException {
        Session session = null;
        List<User> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("status", status));

            users = listAndCast(criteria);
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

    @Override
    public List<User> getByAccess(int access) throws SQLException {
        Session session = null;
        List<User> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("access", access));

            users = listAndCast(criteria);
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

    @Override
    public List<User> getBySession(String uSession) throws SQLException {
        Session session = null;
        List<User> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("session", uSession));

            users = listAndCast(criteria);
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

    @Override
    public List<User> getByToken(String token) throws SQLException {
        Session session = null;
        List<User> users = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("token", token));

            users = listAndCast(criteria);
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
    @Override
    public User findByLoginWithCharacters(String login) throws SQLException {
        Session session = null;
        List<User> users;
        try {
            session = Application.db.openSession();
            session.enableFetchProfile("user-with-characters");
            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("login", login));
            criteria.setMaxResults(1);

            users = listAndCast(criteria);
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
