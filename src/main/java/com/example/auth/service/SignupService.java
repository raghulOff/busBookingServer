package com.example.auth.service;

import com.example.auth.dao.UserDAO;
import com.example.auth.model.User;

public class SignupService {
    public static boolean signupVerification(User user) {
        if (UserDAO.exists(user.getUsername())) {
            return false;
        }
        UserDAO.addUser(user);
        return true;
    }
}


