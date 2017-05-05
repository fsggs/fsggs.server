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
@Table(name = "game_map_galaxies")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GalaxyEntity extends BaseModelEntity implements IGalaxyEntity {
    private String name;
    private String title;
    //private CharacterEntity owner;
    private long ownerId;
    private long universeId;
    //private UniverseEntity universe;
    //private Set<SolarEntity> solars;
    //private Set<MapEntity> maps;
    //private Set<SpaceObjectEntity> objects;

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
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    @ManyToOne
//    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
//    public CharacterEntity getOwner() {
//        return owner;
//    }
//
//    public void setOwner(CharacterEntity owner) {
//        this.owner = owner;
//    }

    @Basic
    @Column(name = "owner_id")
    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    @Basic
    @Column(name = "universe_id")
    public long getUniverseId() {
        return universeId;
    }

    public void setUniverseId(long universeId) {
        this.universeId = universeId;
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
//    @JoinColumn(name = "universe_id", referencedColumnName = "id", nullable = false)
//    public UniverseEntity getUniverse() {
//        return universe;
//    }
//
//    public void setUniverse(UniverseEntity universe) {
//        this.universe = universe;
//    }
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "galaxy", cascade = CascadeType.ALL)
//    public Set<SolarEntity> getSolars() {
//        return solars;
//    }
//
//    public void setSolars(Set<SolarEntity> solars) {
//        this.solars = solars;
//    }
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "galaxy", cascade = CascadeType.ALL)
//    public Set<MapEntity> getMaps() {
//        return maps;
//    }
//
//    public void setMaps(Set<MapEntity> maps) {
//        this.maps = maps;
//    }
//
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "galaxy", cascade = CascadeType.ALL)
//    public Set<SpaceObjectEntity> getObjects() {
//        return objects;
//    }
//
//    public void setObjects(Set<SpaceObjectEntity> objects) {
//        this.objects = objects;
//    }
}
