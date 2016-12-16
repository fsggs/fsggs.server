package com.fsggs.server.models.game.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.core.db.BaseEntity;
import com.fsggs.server.models.game.Character;
import com.fsggs.server.models.game.maps.Galaxy;
import com.fsggs.server.models.game.maps.Solar;
import com.fsggs.server.models.game.maps.Universe;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "game_map_objects")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GameObject extends GameObjectModel implements IGameObject {
    private String name = "AUX-U0G0S0IN";
    private String title = "Anomaly";
    private String metadata = "";
    // private Character owner;
    //private Galaxy galaxy;
    private Long ownerId;
    private long universeId = 0;
    private long galaxyId = 0;
    private long solarId = 0;
    private long posX = 0;
    private long posY = 0;
    private long posZ = 0;
    private long typeId = 0;
    //private Universe universe;
    //private Solar solar;

    private Date createdDate;
    private Date updatedDate;

    public GameObject() {
        super();
    }

    public GameObject(long typeId) {
        this();
        this.typeId = typeId;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public GameObject setName(String name) {
        this.name = name;
        return this;
    }

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public GameObject setTitle(String title) {
        this.title = title;
        return this;
    }

    @Column(name = "metadata", columnDefinition = "TEXT")
    public String getMetadata() {
        return metadata;
    }

    public GameObject setMetadata(String metadata) {
        this.metadata = metadata;
        return this;
    }

//    @ManyToOne
//    @JoinColumn(name = "owner_id", referencedColumnName = "id")
//    public Character getOwner() {
//        return owner;
//    }
//
//    public GameObject setOwner(Character owner) {
//        this.owner = owner;
//        return this;
//    }

//    @ManyToOne
//    @JoinColumn(name = "galaxy_id", referencedColumnName = "id")
//    public Galaxy getGalaxy() {
//        return galaxy;
//    }
//
//    public GameObject setGalaxy(Galaxy galaxy) {
//        this.galaxy = galaxy;
//        return this;
//    }

    @Column(name = "owner_id")
    public Long getOwnerId() {
        return ownerId;
    }

    public GameObject setOwnerId(Long owner) {
        this.ownerId = (owner != null ? owner : 0);
        return this;
    }

    @Column(name = "universe_id")
    public long getUniverseId() {
        return universeId;
    }

    public GameObject setUniverseId(long universeId) {
        this.universeId = universeId;
        return this;
    }

    @Column(name = "galaxy_id")
    public long getGalaxyId() {
        return galaxyId;
    }

    public GameObject setGalaxyId(long galaxyId) {
        this.galaxyId = galaxyId;
        return this;
    }

    @Column(name = "solar_id")
    public long getSolarId() {
        return solarId;
    }

    public GameObject setSolarId(long solarId) {
        this.solarId = solarId;
        return this;
    }

    @Column(name = "posX")
    public long getPosX() {
        return posX;
    }

    public GameObject setPosX(long posX) {
        this.posX = posX;
        return this;
    }

    @Column(name = "posY")
    public long getPosY() {
        return posY;
    }

    public GameObject setPosY(long posY) {
        this.posY = posY;
        return this;
    }

    @Column(name = "posZ")
    public long getPosZ() {
        return posZ;
    }

    public GameObject setPosZ(long posZ) {
        this.posZ = posZ;
        return this;
    }

    @Column(name = "type_id")
    public long getTypeId() {
        return typeId;
    }

    public GameObject setTypeId(long typeId) {
        this.typeId = typeId;
        return this;
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
//    @JoinColumn(name = "universe_id", referencedColumnName = "id")
//    public Universe getUniverse() {
//        return universe;
//    }
//
//    public GameObject setUniverse(Universe universe) {
//        this.universe = universe;
//        return this;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "solar_id", referencedColumnName = "id")
//    public Solar getSolar() {
//        return solar;
//    }
//
//    public GameObject setSolar(Solar solar) {
//        this.solar = solar;
//        return this;
//    }
}
