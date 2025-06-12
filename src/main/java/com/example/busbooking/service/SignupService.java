package com.example.busbooking.service;

import com.example.busbooking.dao.UserDAO;
import com.example.busbooking.model.User;

public class SignupService {
    public static boolean signupVerification(User user) {
        if (UserDAO.exists(user.getUsername())) {
            return false;
        }
        UserDAO.addUser(user);
        return true;
    }
}


