package com.fsggs.server.models.game.objects;

import com.fsggs.server.Application;
import com.fsggs.server.core.db.BaseModelEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpaceObjectModel extends BaseModelEntity {
    static public List<SpaceObjectEntity> getSolarChunk(long universeId, long galaxyId, long solarId) throws SQLException {
        Session session = null;
        List<SpaceObjectEntity> entities = new ArrayList<>();
        try {
            session = Application.db.openSession();
            Criteria criteria = session.createCriteria(SpaceObjectEntity.class);
            criteria.add(Restrictions.eq("universeId", universeId))
                    .add(Restrictions.eq("galaxyId", galaxyId))
                    .add(Restrictions.eq("solarId", solarId));

            entities = SpaceObjectEntity.listAndCast(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            Application.logger.warn("Error when SpaceObjectEntity.getSolarChunk("
                    + universeId + ", "
                    + galaxyId + ", "
                    + solarId + ")");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return entities;
    }
}
