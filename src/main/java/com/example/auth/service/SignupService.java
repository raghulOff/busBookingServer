package com.example.auth.service;

import com.example.auth.dao.UserRepository;
import com.example.auth.model.User;

public class SignupService {
    public static boolean signupVerification(User user) {
        if (UserRepository.exists(user.getUsername())) {
            return false;
        }
        UserRepository.addUser(user);
        return true;
    }
}


