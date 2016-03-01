package com.fsggs.server.entities.master.dao;

import com.fsggs.server.Application;
import com.fsggs.server.entities.master.Server;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServerDAO implements IServerDAO {
    @Override
    public void addServer(Server server) throws SQLException {
        Session session = null;
        try {
            session = Application.db.openSession();
            session.beginTransaction();
            session.save(server);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when insert");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public void updateServer(Server server) throws SQLException {
        Session session = null;
        try {
            session = Application.db.openSession();
            session.beginTransaction();
            session.update(server);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when update");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public void deleteServer(Server server) throws SQLException {
        Session session = null;
        try {
            session = Application.db.openSession();
            session.beginTransaction();
            session.delete(server);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when delete");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public Server getServerById(Long id) throws SQLException {
        Session session = null;
        Server server = null;
        try {
            session = Application.db.openSession();
            server = session.load(Server.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when findById");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return server;
    }

    @Override
    public List<Server> getAllServers() throws SQLException {
        Session session = null;
        List<Server> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(Server.class)
                    .setResultTransformer(Transformers.aliasToBean(Server.class));

            //noinspection unchecked
            servers = criteria.list();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getAll");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return servers;
    }

    @Override
    public List<Server> getAllServersScope(int offset, int limit) throws SQLException {
        Session session = null;
        List<Server> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(Server.class)
                    .setProjection(Projections.projectionList()
                            .add(Projections.property("name"), "name")
                            .add(Projections.property("address"), "address"))
                    .setResultTransformer(Transformers.aliasToBean(Server.class));

            criteria.setFirstResult(offset);
            criteria.setMaxResults(limit);

            //noinspection unchecked
            servers = (List<Server>) criteria.list();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getAllServersScope");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return servers;
    }

    @Override
    public List<Server> getServersByName(String name) throws SQLException {
        Session session = null;
        List<Server> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(Server.class)
                    .add(Restrictions.eq("name", name))
                    .setResultTransformer(Transformers.aliasToBean(Server.class));

            //noinspection unchecked
            servers = criteria.list();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getServersByName");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return servers;
    }

    @Override
    public List<Server> getServersByAddress(String address) throws SQLException {
        Session session = null;
        List<Server> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(Server.class)
                    .add(Restrictions.eq("address", address))
                    .setResultTransformer(Transformers.aliasToBean(Server.class));

            //noinspection unchecked
            servers = criteria.list();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getServersByAddress");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return servers;
    }
}
