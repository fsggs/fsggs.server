package com.fsggs.server.packets.services;

import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseNetworkPacket;
import com.fsggs.server.core.session.SessionManager;
import com.fsggs.server.entities.auth.User;
import com.fsggs.server.packets.AuthPacket;
import com.fsggs.server.utils.EMail;

import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

public class AuthPacketService {
    final private Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private AuthPacket ap;

    public AuthPacketService(AuthPacket ap) {
        this.ap = ap;
    }

    public void tryAuthWithLogin(String login, String password) {
        if (!Objects.equals(login, "")) {
            try {
                User user = Application.dao.getUser().findByLoginWithCharacters(login);
                if (user != null) {
                    if (checkPassword(password, user.getPassword())) {
                        String session = BaseNetworkPacket.md5(login + String.valueOf(System.currentTimeMillis()));
                        user.setSession(session);
                        user.setLoginDate(new Date());
                        if (!Objects.equals(user.getToken(), "")) user.setToken("");

                        Application.dao.getUser().update(user);
                        String encodedSession = BaseNetworkPacket.md5(session + f.format(user.getLoginDate()));

                        ap.setAction(1);
                        ap.setPassword("");
                        ap.setToken("");
                        ap.setId(user.getId());
                        ap.setSession(encodedSession);
                        ap.setCharacters(user.getCharacters());
                        ap.setResult(true);

                        SessionManager.logout(ap.getContext().channel());
                        SessionManager.update(ap.getContext().channel(), encodedSession, login);
                        ap.updateIdentity();
                    } else ap.addError("Password mismatch");
                } else ap.addError("User not found");
            } catch (Exception e) {
                e.printStackTrace();
                ap.setResult(false);
                ap.addError("SQL Error");
            }
        } else ap.addError("Login must be not clear");
    }

    public void tryRegister(String login, String password) {
        if (!Objects.equals(login, "")) {
            try {
                User user = Application.dao.getUser().findByLogin(login);
                if (user == null) {
                    user = new User(login, encryptPassword(password));
                    user.setAccess(1);
                    if (!AuthPacket.AUTO_LOGIN) {
                        String token;
                        user.setStatus(0);
                        token = BaseNetworkPacket.md5(login + String.valueOf(System.currentTimeMillis()));
                        user.setToken(token);
                        EMail.send(login, "Activate FSGGS Account", "Your token: " + token, "fsggs@localhost");
                    } else {
                        user.setStatus(1);
                    }
                    Application.dao.getUser().add(user);

                    ap.setPassword("");
                    ap.setToken("");
                    ap.setResult(true);

                    if (AuthPacket.AUTO_LOGIN) tryAuthWithLogin(login, password);
                } else ap.addError("User exists");
            } catch (Exception e) {
                e.printStackTrace();
                ap.setResult(false);
                ap.addError("SQL Error");
            }
        } else ap.addError("Login must be not clear");
    }

    public void tryActivateByToken(String login, String token) {
        if (!Objects.equals(login, "")) {
            try {
                User user = Application.dao.getUser().findByLoginWithToken(login, token);
                if (user != null) {
                    user.setToken("");
                    user.setStatus(1);
                    Application.dao.getUser().update(user);
                    ap.setResult(true);
                } else ap.addError("User with token not found");
            } catch (Exception e) {
                e.printStackTrace();
                ap.setResult(false);
                ap.addError("SQL Error");
            }
        } else ap.addError("Login must be not clear");
    }

    public void tryRememberPassword(String login, String token, String password) {
        if (!Objects.equals(login, "")) {
            try {
                User user;
                if (Objects.equals(token, "")) {
                    user = Application.dao.getUser().findByLogin(login);
                    if (user != null) {
                        token = BaseNetworkPacket.md5(login + String.valueOf(System.currentTimeMillis()));
                        user.setToken(token);
                        EMail.send(login, "Reset FSGGS Account Password", "Your token: " + token, "fsggs@localhost");
                        ap.setResult(true);
                        Application.dao.getUser().update(user);
                    } else ap.addError("User not found");
                } else {
                    user = Application.dao.getUser().findByLoginWithToken(login, token, 1);
                    if (user != null) {
                        user.setPassword(encryptPassword(password));
                        user.setToken("");
                        ap.setResult(true);
                        Application.dao.getUser().update(user);
                    } else ap.addError("User with token not found");
                }
                ap.setToken("");
            } catch (Exception e) {
                e.printStackTrace();
                ap.setResult(false);
                ap.addError("SQL Error");
            }
        } else ap.addError("Login must be not clear");
    }

    public void tryChangePassword(String password, String token) {
        if (!ap.getAuth().isGuest()) {
            String login = ap.getAuth().getUser().getLogin();
            User user = ap.getAuth().getUser();
            if (!Objects.equals(token, "")) {
                try {
                    if (Objects.equals(user.getToken(), token)) {
                        user.setPassword(encryptPassword(password));
                        user.setToken("");
                        Application.dao.getUser().update(user);

                        ap.setToken("");
                        ap.setPassword("");
                        ap.setResult(true);
                    } else ap.addError("Token not found");
                } catch (SQLException e) {
                    e.printStackTrace();
                    ap.setResult(false);
                }
            } else {
                try {
                    if (Objects.equals(user.getPassword(), encryptPassword(password))) {
                        token = BaseNetworkPacket.md5(login + String.valueOf(System.currentTimeMillis()));

                        user.setToken(token);
                        Application.dao.getUser().update(user);

                        ap.setToken(token);
                        ap.setResult(true);
                    } else ap.addError("Current password not match");
                    ap.setPassword("");
                } catch (Exception e) {
                    e.printStackTrace();
                    ap.setResult(false);
                }
            }
        } else ap.addError("You must be authorized");
    }

    public void tryReconnect(String login, String session) {
        if (!Objects.equals(login, "") && !Objects.equals(session, "")) {
            try {
                User user = Application.dao.getUser().findByLogin(login);
                if (user != null) {
                    if (Objects.equals(BaseNetworkPacket.md5(user.getSession() + f.format(user.getLoginDate())), session)) {

                        String s = BaseNetworkPacket.md5(login + String.valueOf(System.currentTimeMillis()));
                        user.setSession(s);
                        user.setLoginDate(new Date());
                        if (!Objects.equals(user.getToken(), "")) user.setToken("");

                        Application.dao.getUser().update(user);
                        String encodedSession = BaseNetworkPacket.md5(s + f.format(user.getLoginDate()));

                        ap.setResult(true);

                        SessionManager.logout(ap.getContext().channel());
                        SessionManager.update(ap.getContext().channel(), encodedSession, login);
                        ap.updateIdentity();
                    } else ap.addError("Incorrect reconnection");
                } else ap.addError("Incorrect reconnection");
            } catch (Exception e) {
                e.printStackTrace();
                ap.setResult(false);
            }
        } else ap.addError("Incorrect reconnection");
    }

    public void tryLogout() {
        if (!ap.getAuth().isGuest()) {
            SessionManager.logout(ap.getContext().channel());
            ap.setSession("");
            ap.updateIdentity();
            ap.setResult(true);
        } else ap.addError("You must be authorized");
    }

    private String encryptPassword(String password) {
        return password;
    }

    private boolean checkPassword(String password, String userPassword) {
        return Objects.equals(password, userPassword);
    }
}
