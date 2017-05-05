package com.fsggs.server.models.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.models.game.CharacterEntity;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;

@Entity
@Table(name = "auth_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@FetchProfile(name = "user-with-characters", fetchOverrides = {
        @FetchProfile.FetchOverride(entity = UserEntity.class, association = "characters", mode = FetchMode.JOIN)
})
public class UserEntity extends UserModel implements IUserEntity {
    private String login;
    private String password;
    private String session;
    private String token;
    private int status;
    private int access;

    private Date loginDate;
    private Date registerDate;
    private Date updatedDate;

    private Set<CharacterEntity> characters = new LinkedHashSet<>();

    public UserEntity() {
    }

    public UserEntity(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Basic
    @JsonProperty
    @Column(name = "login", nullable = false)
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Basic
    @JsonIgnore
    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @JsonIgnore
    @Column(name = "session")
    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Basic
    @JsonIgnore
    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Basic
    @JsonProperty
    @Column(name = "status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Basic
    @JsonProperty
    @Column(name = "access", nullable = false)
    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    @JsonProperty
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    public Set<CharacterEntity> getCharacters() {
        return characters;
    }

    public void setCharacters(Set<CharacterEntity> characters) {
        this.characters = characters;
    }

    @Basic
    @JsonProperty
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    @Basic
    @JsonProperty
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_at")
    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    @Basic
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

    @Override
    public String toString() {
        return "UserEntity{" +
                "login='" + login + '\'' +
                ", loginDate=" + loginDate +
                ", registerDate=" + registerDate +
                ", updatedDate=" + updatedDate +
                ", status=" + status +
                ", access=" + access +
                '}';
    }
}
