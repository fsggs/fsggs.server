package com.fsggs.server.entities.auth;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity()
@Table(name = "auth_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class User implements Serializable {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "id")
    private Long id;

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

    @Column(name = "session")
    private int session;

    @Column(name = "token")
    private int token;

    @OneToMany(cascade={CascadeType.ALL})
    @JoinColumn(name="user_id")
    private List<Authority> authority;

    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
