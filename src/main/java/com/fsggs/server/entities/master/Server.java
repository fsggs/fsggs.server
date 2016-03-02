package com.fsggs.server.entities.master;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.core.db.BaseModel;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity()
@Table(name = "master_server")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Server extends BaseModel {
    @JsonProperty
    @Column(name = "name")
    private String name;

    @JsonProperty
    @Column(name = "address")
    private String address;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdDate;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedDate;

    public Server() {
    }

    public Server(String name, String address) {
        this.name = name;
        this.address = address;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


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
