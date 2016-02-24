package com.fsggs.server.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity()
@Table(name = "auth_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class User implements Serializable {
    private Long id;

    private String login;
    private String password;

    private Date registerDate;
    private Date loginDate;

    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    public Date getRegisterTime() {
        return registerDate;
    }

    public void setRegisterTime(Date registerDate) {
        this.registerDate = registerDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_at")
    public Date getLoginTime() {
        return loginDate;
    }

    public void setLoginTime(Date loginDate) {
        this.loginDate = loginDate;
    }
}
