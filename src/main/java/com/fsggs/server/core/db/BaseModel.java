package com.fsggs.server.core.db;

import org.hibernate.Criteria;
import org.hibernate.Query;

import java.util.List;

public class BaseModel {

    protected static <T> List<T> listAndCast(Query q) {
        @SuppressWarnings("unchecked")
        List<T> list = q.list();
        return list;
    }

    protected static <T> List<T> listAndCast(Criteria cr) {
        @SuppressWarnings("unchecked")
        List<T> list = cr.list();
        return list;
    }
}
