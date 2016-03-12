package com.fsggs.server.entities.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.core.db.BaseEntity;
import com.fsggs.server.entities.auth.User;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "game_characters")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Character extends BaseEntity {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    User user;

    @JsonProperty
    @Column(name = "name")
    String name;

    @JsonIgnore
    @Column(name = "password")
    String password = "";

    @JsonProperty
    @Column(name = "race")
    int race;

    //long currentOrbitalObject;
    //List orbitalObjects = new LinkedList<>();
    //List opennedGUI = new LinkedList<>();
    //List quests = new LinkedList<>();

    @JsonProperty
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdDate;

    @JsonProperty
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedDate;

    public Character() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRace() {
        return race;
    }

    public void setRace(int race) {
        this.race = race;
    }

//    public long getCurrentOrbitalObject() {
//        return currentOrbitalObject;
//    }
//
//    public void setCurrentOrbitalObject(long currentOrbitalObject) {
//        this.currentOrbitalObject = currentOrbitalObject;
//    }
//
//    public List getOrbitalObjects() {
//        return orbitalObjects;
//    }
//
//    public void setOrbitalObjects(List orbitalObjects) {
//        this.orbitalObjects = orbitalObjects;
//    }
//
//    public List getOpennedGUI() {
//        return opennedGUI;
//    }
//
//    public void setOpennedGUI(List opennedGUI) {
//        this.opennedGUI = opennedGUI;
//    }
//
//    public List getQuests() {
//        return quests;
//    }
//
//    public void setQuests(List quests) {
//        this.quests = quests;
//    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
