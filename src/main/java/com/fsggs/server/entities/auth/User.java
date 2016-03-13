package com.fsggs.server.entities.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.core.db.BaseEntity;
import com.fsggs.server.entities.game.Character;
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
        @FetchProfile.FetchOverride(entity = User.class, association = "characters", mode = FetchMode.JOIN)
})
public class User extends BaseEntity {
    @JsonProperty
    @Column(name = "login")
    private String login;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @JsonProperty
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_at")
    private Date loginDate;

    @JsonProperty
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date registerDate;

    @JsonProperty
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedDate;

    @JsonProperty
    @Column(name = "status")
    private int status;

    @JsonProperty
    @Column(name = "access")
    private int access;

    @JsonIgnore
    @Column(name = "session")
    private String session;

    @JsonIgnore
    @Column(name = "token")
    private String token;

    @JsonProperty
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Character> characters = new LinkedHashSet<>();


    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }


    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }


    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Set<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(Set<Character> characters) {
        this.characters = characters;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", loginDate=" + loginDate +
                ", registerDate=" + registerDate +
                ", updatedDate=" + updatedDate +
                ", status=" + status +
                ", access=" + access +
                ", session='" + session + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
