package com.fsggs.server.entities.auth;

import com.fsggs.server.core.db.BaseModel;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity()
@Table(name = "auth_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends BaseModel {
    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date registerDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_at")
    private Date loginDate;

    @Column(name = "status")
    private int status;

    @Column(name = "access")
    private int access;

    @Column(name = "session")
    private int session;

    @Column(name = "token")
    private int token;

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


    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }


    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }
}
