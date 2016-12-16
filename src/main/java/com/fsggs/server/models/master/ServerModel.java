package com.fsggs.server.models.master;

import com.fsggs.server.Application;
import com.fsggs.server.core.db.BaseEntity;
import com.fsggs.server.core.db.IBaseEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

abstract class ServerModel extends BaseEntity {
    static public List<Server> getByName(String name) throws SQLException {
        Session session = null;
        List<Server> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(Server.class)
                    .add(Restrictions.eq("name", name));

            servers = IBaseEntity.listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getByName()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return servers;
    }

    static public List<Server> getByAddress(String address) throws SQLException {
        Session session = null;
        List<Server> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(Server.class)
                    .add(Restrictions.eq("address", address));

            servers = IBaseEntity.listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getByAddress()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return servers;
    }

    static public List<Server> getByToken(String token) throws SQLException {
        final long ONE_MINUTE_IN_MILLIS = 60000;
        Calendar date = Calendar.getInstance();
        long currentTime = date.getTimeInMillis();
        Date lastHalfMinute = new Date(currentTime - ONE_MINUTE_IN_MILLIS / 2);

        Session session = null;
        List<Server> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(Server.class)
                    .add(Restrictions.eq("token", token))
                    .add(Restrictions.le("updatedDate", lastHalfMinute));

            servers = IBaseEntity.listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getByToken()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return servers;
    }

    /* Customs */
    static public List<Server> getAllScope(int offset, int limit) throws SQLException {
        final long ONE_MINUTE_IN_MILLIS = 60000;
        Calendar date = Calendar.getInstance();
        long currentTime = date.getTimeInMillis();
        Date lastMinute = new Date(currentTime - ONE_MINUTE_IN_MILLIS);

        Session session = null;
        List<Server> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(Server.class)
                    .setProjection(Projections.projectionList()
                            .add(Projections.property("name"), "name")
                            .add(Projections.property("address"), "address")
                            .add(Projections.property("updatedDate"), "updatedDate"))
                    .add(Restrictions.gt("updatedDate", lastMinute))
                    .setResultTransformer(Transformers.aliasToBean(Server.class));

            criteria.setFirstResult(offset);
            criteria.setMaxResults(limit);

            servers = IBaseEntity.listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getAllScope()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return servers;
    }
}
