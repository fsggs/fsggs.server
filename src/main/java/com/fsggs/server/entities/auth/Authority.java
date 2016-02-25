package com.fsggs.server.entities.auth;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity()
@Table(name = "auth_assignment")
public class Authority implements Serializable {
    @Id
    @Column(name = "user_id")
    private Long user;

    @Id
    @Column(name = "group_id")
    private Long group;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdDate;

    public Authority() {
    }

    public Authority(Long user, Long group) {
        this.user = user;
        this.group = group;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getGroup() {
        return group;
    }

    public void setGroup(Long group) {
        this.group = group;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
