package com.fsggs.server.packets.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseNetworkPacket;
import com.fsggs.server.core.session.SessionManager;
import com.fsggs.server.entities.auth.User;
import com.fsggs.server.entities.game.Character;
import com.fsggs.server.packets.AuthPacket;
import com.fsggs.server.utils.EMail;

import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.fsggs.server.packets.services.AuthPacketService.APSError.*;


public class AuthPacketService {
    final private Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private AuthPacket ap;

    public AuthPacketService(AuthPacket ap) {
        this.ap = ap;
    }

    public APSR_AuthWithLogin tryAuthWithLogin(String login, String password) {
        APSR_AuthWithLogin response = new APSR_AuthWithLogin(ap.getPacketName(), ap.getAction());
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

                        ap.setAction(response.action = 1);
                        response.id = user.getId();
                        response.session = encodedSession;
                        response.characters = user.getCharacters();
                        response.result = true;

                        SessionManager.logout(ap.getContext().channel());
                        SessionManager.update(ap.getContext().channel(), encodedSession, login);
                        ap.updateIdentity();
                    } else response.addError(E_PASSWORD_MISMATCH);
                } else response.addError(E_USER_NOT_FOUND);
            } catch (Exception e) {
                e.printStackTrace();
                response.result = false;
                response.addError(E_SQL_ERROR);
            }
        } else response.addError(E_LOGIN_IS_EMPTY);
        return response;
    }

    public APSResponse tryRegister(String login, String password) {
        APSResponse response = new APSResponse(ap.getPacketName(), ap.getAction());
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

                    response.result = true;
                    if (AuthPacket.AUTO_LOGIN) tryAuthWithLogin(login, password);
                } else response.addError(E_USER_EXIST);
            } catch (Exception e) {
                e.printStackTrace();
                response.result = false;
                response.addError(E_SQL_ERROR);
            }
        } else response.addError(E_LOGIN_IS_EMPTY);
        return response;
    }

    public APSResponse tryActivateByToken(String login, String token) {
        APSResponse response = new APSResponse(ap.getPacketName(), ap.getAction());
        if (!Objects.equals(login, "")) {
            try {
                User user = Application.dao.getUser().findByLoginWithToken(login, token);
                if (user != null) {
                    user.setStatus(1);
                    Application.dao.getUser().update(user);
                    response.result = true;
                } else response.addError(E_USER_NOT_FOUND_BY_TOKEN);
            } catch (Exception e) {
                e.printStackTrace();
                response.result = false;
                response.addError(E_SQL_ERROR);
            }
        } else response.addError(E_LOGIN_IS_EMPTY);
        return response;
    }

    public APSResponse tryRememberPassword(String login, String token, String password) {
        APSResponse response = new APSResponse(ap.getPacketName(), ap.getAction());
        if (!Objects.equals(login, "")) {
            try {
                User user;
                if (Objects.equals(token, "")) {
                    user = Application.dao.getUser().findByLogin(login);
                    if (user != null) {
                        token = BaseNetworkPacket.md5(login + String.valueOf(System.currentTimeMillis()));
                        user.setToken(token);
                        EMail.send(login, "Reset FSGGS Account Password", "Your token: " + token, "fsggs@localhost");
                        response.result = true;
                        Application.dao.getUser().update(user);
                    } else response.addError(E_USER_NOT_FOUND);
                } else {
                    user = Application.dao.getUser().findByLoginWithToken(login, token, 1);
                    if (user != null) {
                        user.setPassword(encryptPassword(password));
                        user.setToken("");
                        response.result = true;
                        Application.dao.getUser().update(user);
                    } else response.addError(E_USER_NOT_FOUND_BY_TOKEN);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.result = false;
                response.addError(E_SQL_ERROR);
            }
        } else response.addError(E_LOGIN_IS_EMPTY);
        return response;
    }

    public APSR_ChangePassword tryChangePassword(String password, String token) {
        APSR_ChangePassword response = new APSR_ChangePassword(ap.getPacketName(), ap.getAction());
        if (!ap.getAuth().isGuest()) {
            String login = ap.getAuth().getUser().getLogin();
            User user = ap.getAuth().getUser();
            if (!Objects.equals(token, "")) {
                try {
                    if (Objects.equals(user.getToken(), token)) {
                        user.setPassword(encryptPassword(password));
                        user.setToken("");
                        Application.dao.getUser().update(user);

                        response.result = true;
                    } else response.addError(E_TOKEN_NOT_FOUND);
                } catch (SQLException e) {
                    e.printStackTrace();
                    response.result = false;
                }
            } else {
                try {
                    if (Objects.equals(user.getPassword(), encryptPassword(password))) {
                        token = BaseNetworkPacket.md5(login + String.valueOf(System.currentTimeMillis()));

                        user.setToken(token);
                        Application.dao.getUser().update(user);

                        response.token = token;
                        response.result = true;
                    } else response.addError(E_CURRENT_PASSWORD_MISMATCH);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.result = false;
                }
            }
        } else response.addError(E_MUST_BE_AUTHORIZED);
        return response;
    }

    public APSResponse tryReconnect(String login, String session) {
        APSResponse response = new APSResponse(ap.getPacketName(), ap.getAction());
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

                        response.result = true;

                        SessionManager.logout(ap.getContext().channel());
                        SessionManager.update(ap.getContext().channel(), encodedSession, login);
                        ap.updateIdentity();
                    } else response.addError(E_INCORRECT_RECONNECTION);
                } else response.addError(E_INCORRECT_RECONNECTION);
            } catch (Exception e) {
                e.printStackTrace();
                response.result = false;
            }
        } else response.addError(E_INCORRECT_RECONNECTION);
        return response;
    }

    public APSResponse tryLogout() {
        APSResponse response = new APSResponse(ap.getPacketName(), ap.getAction());
        if (!ap.getAuth().isGuest()) {
            SessionManager.logout(ap.getContext().channel());
            ap.updateIdentity();
            response.result = true;
        } else response.addError(E_MUST_BE_AUTHORIZED);
        return response;
    }

    private String encryptPassword(String password) {
        return password;
    }

    private boolean checkPassword(String password, String userPassword) {
        return Objects.equals(password, userPassword);
    }

    static public class APSResponse {
        @JsonProperty("packet")
        public String packet;

        @JsonProperty("action")
        public int action;

        @JsonProperty("errors")
        public List<Integer> errors = new ArrayList<>();

        @JsonProperty("result")
        public boolean result = false;

        public APSResponse(String packet, int action) {
            this.packet = packet;
            this.action = action;
        }

        public void addError(APSError error) {
            if (!errors.contains(error.getId())) {
                errors.add(error.getId());
            }
        }
    }

    private class APSR_AuthWithLogin extends APSResponse {
        @JsonProperty("id")
        public long id;

        @JsonProperty("session")
        public String session;

        @JsonProperty("characters")
        public Set<Character> characters = new LinkedHashSet<>();

        APSR_AuthWithLogin(String packet, int action) {
            super(packet, action);
        }
    }

    private class APSR_ChangePassword extends APSResponse {
        @JsonProperty("token")
        public String token;

        APSR_ChangePassword(String packet, int action) {
            super(packet, action);
        }
    }

    public enum APSError {
        E_PASSWORD_MISMATCH(0, "Password mismatch"),
        E_USER_NOT_FOUND(1, "User not found"),
        E_SQL_ERROR(2, "SQL Error"),
        E_LOGIN_IS_EMPTY(3, "Login must be not clear"),
        E_USER_EXIST(4, "User exists"),
        E_USER_NOT_FOUND_BY_TOKEN(5, "User with token not found"),
        E_TOKEN_NOT_FOUND(6, "Token not found"),
        E_CURRENT_PASSWORD_MISMATCH(7, "Current password not match"),
        E_MUST_BE_AUTHORIZED(8, "You must be authorized"),
        E_INCORRECT_RECONNECTION(9, "Incorrect reconnection"),
        E_UNKNOWN_ACTION(10, "Unknown action");

        private int id;
        private String message;

        APSError(int id, String message) {
            this.id = id;
            this.message = message;
        }

        public int getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }
    }
}
