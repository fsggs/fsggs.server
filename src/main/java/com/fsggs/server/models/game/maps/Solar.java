package com.fsggs.server.models.game.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.core.db.BaseEntity;
import com.fsggs.server.models.game.Character;
import com.fsggs.server.models.game.objects.GameObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "game_map_solars")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Solar extends BaseEntity implements ISolar {
    private String name;
    private String title;
    //private Character owner;
    //private Galaxy galaxy;
    private long ownerId;
    private long universeId;
    private long galaxyId;
    //private Universe universe;
    //private Set<Map> maps;
    //private Set<GameObject> objects;

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
//    public Character getGameCharactersByOwner() {
//        return owner;
//    }
//
//    public void setGameCharactersByOwner(Character owner) {
//        this.owner = owner;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "galaxy_id", referencedColumnName = "id", nullable = false)
//    public Galaxy getGalaxy() {
//        return galaxy;
//    }
//
//    public void setGalaxy(Galaxy galaxy) {
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
//    public Universe getUniverse() {
//        return universe;
//    }
//
//    public void setUniverse(Universe universe) {
//        this.universe = universe;
//    }
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "solar", cascade = CascadeType.ALL)
//    public Set<Map> getMaps() {
//        return maps;
//    }
//
//    public void setMaps(Set<Map> maps) {
//        this.maps = maps;
//    }
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "solar", cascade = CascadeType.ALL)
//    public Set<GameObject> getObjects() {
//        return objects;
//    }
//
//    public void setObjects(Set<GameObject> objects) {
//        this.objects = objects;
//    }
}
