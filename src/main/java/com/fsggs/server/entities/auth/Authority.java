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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Authority authority = (Authority) o;

        return user != null ? user.equals(authority.user) : authority.user == null
                && (group != null ? group.equals(authority.group) : authority.group == null
                && (createdDate != null ? createdDate.equals(authority.createdDate) : authority.createdDate == null
        ));
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
}
