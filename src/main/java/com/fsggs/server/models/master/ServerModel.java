package com.fsggs.server.models.master;

import com.fsggs.server.Application;
import com.fsggs.server.core.db.BaseModelEntity;
import com.fsggs.server.core.db.IBaseModelEntity;
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

abstract class ServerModel extends BaseModelEntity {
    static public List<ServerEntity> getByName(String name) throws SQLException {
        Session session = null;
        List<ServerEntity> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(ServerEntity.class)
                    .add(Restrictions.eq("name", name));

            servers = ServerModel.listAndCast(criteria);
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

    static public List<ServerEntity> getByAddress(String address) throws SQLException {
        Session session = null;
        List<ServerEntity> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(ServerEntity.class)
                    .add(Restrictions.eq("address", address));

            servers = ServerModel.listAndCast(criteria);
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

    static public List<ServerEntity> getByToken(String token) throws SQLException {
        final long ONE_MINUTE_IN_MILLIS = 60000;
        Calendar date = Calendar.getInstance();
        long currentTime = date.getTimeInMillis();
        Date lastHalfMinute = new Date(currentTime - ONE_MINUTE_IN_MILLIS / 2);

        Session session = null;
        List<ServerEntity> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(ServerEntity.class)
                    .add(Restrictions.eq("token", token))
                    .add(Restrictions.le("updatedDate", lastHalfMinute));

            servers = ServerModel.listAndCast(criteria);
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
    static public List<ServerEntity> getAllScope(int offset, int limit) throws SQLException {
        final long ONE_MINUTE_IN_MILLIS = 60000;
        Calendar date = Calendar.getInstance();
        long currentTime = date.getTimeInMillis();
        Date lastMinute = new Date(currentTime - ONE_MINUTE_IN_MILLIS);

        Session session = null;
        List<ServerEntity> servers = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(ServerEntity.class)
                    .setProjection(Projections.projectionList()
                            .add(Projections.property("name"), "name")
                            .add(Projections.property("address"), "address")
                            .add(Projections.property("updatedDate"), "updatedDate"))
                    .add(Restrictions.gt("updatedDate", lastMinute))
                    .setResultTransformer(Transformers.aliasToBean(ServerEntity.class));

            criteria.setFirstResult(offset);
            criteria.setMaxResults(limit);

            servers = ServerModel.listAndCast(criteria);
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
