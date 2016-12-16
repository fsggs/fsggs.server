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
@Table(name = "game_map_universes")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Universe extends BaseEntity implements IUniverse {
    private String name;
    private String title;
    //private Character owner;
    private long ownerId;
    //private Set<Galaxy> galaxies;
    //private Set<Solar> solars;
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
//    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
//    public Character getOwner() {
//        return owner;
//    }
//
//    public void setOwner(Character owner) {
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
//    public Set<Galaxy> getGalaxies() {
//        return galaxies;
//    }
//
//    public void setGalaxies(Set<Galaxy> galaxies) {
//        this.galaxies = galaxies;
//    }
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "universe", cascade = CascadeType.ALL)
//    public Set<Solar> getSolars() {
//        return solars;
//    }
//
//    public void setSolars(Set<Solar> solars) {
//        this.solars = solars;
//    }
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "universe", cascade = CascadeType.ALL)
//    public Set<Map> getMaps() {
//        return maps;
//    }
//
//    public void setMaps(Set<Map> maps) {
//        this.maps = maps;
//    }
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "universe", cascade = CascadeType.ALL)
//    public Set<GameObject> getObjects() {
//        return objects;
//    }
//
//    public void setObjects(Set<GameObject> objects) {
//        this.objects = objects;
//    }
}
