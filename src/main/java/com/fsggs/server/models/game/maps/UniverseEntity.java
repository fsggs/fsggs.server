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
@Table(name = "game_map_universes")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UniverseEntity extends BaseModelEntity implements IUniverseEntity {
    private String name;
    private String title;
    //private CharacterEntity owner;
    private long ownerId;
    //private Set<GalaxyEntity> galaxies;
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
//    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
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

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "universe", cascade = CascadeType.ALL)
//    public Set<GalaxyEntity> getGalaxies() {
//        return galaxies;
//    }
//
//    public void setGalaxies(Set<GalaxyEntity> galaxies) {
//        this.galaxies = galaxies;
//    }
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "universe", cascade = CascadeType.ALL)
//    public Set<SolarEntity> getSolars() {
//        return solars;
//    }
//
//    public void setSolars(Set<SolarEntity> solars) {
//        this.solars = solars;
//    }
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "universe", cascade = CascadeType.ALL)
//    public Set<MapEntity> getMaps() {
//        return maps;
//    }
//
//    public void setMaps(Set<MapEntity> maps) {
//        this.maps = maps;
//    }
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "universe", cascade = CascadeType.ALL)
//    public Set<SpaceObjectEntity> getObjects() {
//        return objects;
//    }
//
//    public void setObjects(Set<SpaceObjectEntity> objects) {
//        this.objects = objects;
//    }
}
