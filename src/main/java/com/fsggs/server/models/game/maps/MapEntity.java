package com.fsggs.server.models.game.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.core.db.BaseModelEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "game_maps")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MapEntity extends BaseModelEntity implements IMapEntity {
    private String name;
    private String metadata;
    private int version;
    private UniverseEntity universe;
    private GalaxyEntity galaxyEntity;
    private SolarEntity solar;
    private Long universeId;
    private Long galaxyId;
    private Long solarId;

    private Date createdDate;
    private Date updatedDate;

    @Basic
    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "metadata", columnDefinition = "TEXT")
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @Basic
    @Column(name = "version", nullable = false)
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @JsonProperty
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @JsonProperty
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

//    @ManyToOne
//    @JoinColumn(name = "universe_id", referencedColumnName = "id", insertable = false, updatable = false)
//    public UniverseEntity getUniverse() {
//        return universe;
//    }
//
//    public void setUniverse(UniverseEntity universe) {
//        this.universe = universe;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "galaxy_id", referencedColumnName = "id", insertable = false, updatable = false)
//    public GalaxyEntity getGalaxy() {
//        return galaxyEntity;
//    }
//
//    public void setGalaxy(GalaxyEntity galaxyEntity) {
//        this.galaxyEntity = galaxyEntity;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "solar_id", referencedColumnName = "id", insertable = false, updatable = false)
//    public SolarEntity getSolar() {
//        return solar;
//    }
//
//    public void setSolar(SolarEntity solar) {
//        this.solar = solar;
//    }

    @Basic
    @Column(name = "universe_id", insertable = false, updatable = false)
    public Long getUniverseId() {
        return universeId;
    }

    public void setUniverseId(Long universeId) {
        this.universeId = universeId;
    }

    @Basic
    @Column(name = "galaxy_id", insertable = false, updatable = false)
    public Long getGalaxyId() {
        return galaxyId;
    }

    public void setGalaxyId(Long galaxyId) {
        this.galaxyId = galaxyId;
    }

    @Basic
    @Column(name = "solar_id", insertable = false, updatable = false)
    public Long getSolarId() {
        return solarId;
    }

    public void setSolarId(Long solarId) {
        this.solarId = solarId;
    }
}
