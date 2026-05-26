package com.possystem.sajilopos.service;

import com.possystem.sajilopos.dao.UserDAO;
import com.possystem.sajilopos.model.User;

import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private User loggedInUser = null;

    public boolean login(String username, String password) {
        User user = userDAO.getUserByUsername(username);

        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            loggedInUser = user;
            return true;
        }

        return false;
    }

    public void logout() {
        loggedInUser = null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isAdmin() {
        return loggedInUser != null && loggedInUser.getRole().equals("admin");
    }

    public boolean isStaff() {
        return loggedInUser != null && loggedInUser.getRole().equals("staff");
    }
}
