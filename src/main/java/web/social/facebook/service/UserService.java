package web.social.facebook.service;

import web.social.facebook.entities.User;

import java.sql.SQLException;

public interface UserService {
    User findByEmail(String email);

    void registerAccount(User user) throws SQLException;
}
