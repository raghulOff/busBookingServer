package com.example.busbooking.service;

import com.example.busbooking.dao.base.UserDAO;
import com.example.busbooking.db.DBConnection;
import com.example.busbooking.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SignupService {
    public static final String check_user_exist_query = "SELECT 1 FROM users WHERE username = ?";

    public static boolean signupVerification( User user ) {
        if (exists(user.getUsername())) {
            return false;
        }
        try {
            UserDAO.addUser(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // to check whether a user exists or not
    public static boolean exists( String username ) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(check_user_exist_query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
}


