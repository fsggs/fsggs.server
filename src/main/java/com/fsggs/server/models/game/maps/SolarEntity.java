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
@Table(name = "game_map_solars")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SolarEntity extends BaseModelEntity implements ISolarEntity {
    private String name;
    private String title;
    //private CharacterEntity owner;
    //private GalaxyEntity galaxy;
    private long ownerId;
    private long universeId;
    private long galaxyId;
    //private UniverseEntity universe;
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
//    public CharacterEntity getGameCharactersByOwner() {
//        return owner;
//    }
//
//    public void setGameCharactersByOwner(CharacterEntity owner) {
//        this.owner = owner;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "galaxy_id", referencedColumnName = "id", nullable = false)
//    public GalaxyEntity getGalaxy() {
//        return galaxy;
//    }
//
//    public void setGalaxy(GalaxyEntity galaxy) {
//        this.galaxy = galaxy;
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

    @Basic
    @Column(name = "galaxy_id")
    public long getGalaxyId() {
        return galaxyId;
    }

    public void setGalaxyId(long galaxyId) {
        this.galaxyId = galaxyId;
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
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "solar", cascade = CascadeType.ALL)
//    public Set<MapEntity> getMaps() {
//        return maps;
//    }
//
//    public void setMaps(Set<MapEntity> maps) {
//        this.maps = maps;
//    }
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "solar", cascade = CascadeType.ALL)
//    public Set<SpaceObjectEntity> getObjects() {
//        return objects;
//    }
//
//    public void setObjects(Set<SpaceObjectEntity> objects) {
//        this.objects = objects;
//    }
}
