package com.fsggs.server.core.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.Application;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
abstract public class BaseModelEntity implements IBaseModelEntity, Serializable {
    private Long id;

    @Id
    @JsonProperty
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static <T> List<T> listAndCast(Query q) {
        @SuppressWarnings("unchecked")
        List<T> list = q.list();
        return list;
    }

    public static <T> List<T> listAndCast(Criteria cr) {
        @SuppressWarnings("unchecked")
        List<T> list = cr.list();
        return list;
    }

    public void save() throws SQLException {
        if (getId() != null) {
            BaseModelEntity.update(this);
        } else {
            BaseModelEntity.add(this);
        }
    }

    static public void add(BaseModelEntity entity) throws SQLException {
        Session session = null;
        try {
            session = Application.db.openSession();
            session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when insert entity");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    static public void update(BaseModelEntity entity) throws SQLException {
        Session session = null;
        try {
            session = Application.db.openSession();
            session.beginTransaction();
            session.update(entity);
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

    static public void delete(BaseModelEntity entity) throws SQLException {
        Session session = null;
        try {
            session = Application.db.openSession();
            session.beginTransaction();
            session.delete(entity);
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

    static public BaseModelEntity getById(Long id) throws SQLException {
        Session session = null;
        BaseModelEntity entity = null;
        try {
            session = Application.db.openSession();
            entity = session.get(BaseModelEntity.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getById()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return entity;
    }

    static public List<BaseModelEntity> getAll() throws SQLException {
        Session session = null;
        List<BaseModelEntity> entities = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(BaseModelEntity.class);

            entities = listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when getAll()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return entities;
    }
}
