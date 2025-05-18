package com.example.auth;

import java.util.HashMap;
import java.util.Map;

public class AuthRepo {
    static Map<String, String> usersList = new HashMap<>();

    static public boolean storeUser(User user) {

        String user_name = user.getUsername();
        String user_password = user.getPassword();

        if (usersList.containsKey(user_name)) {

            return false;
        } else {

            usersList.put(user_name, user_password);
            return true;
        }
    }

    static public boolean loginUser(User user) {
        String user_name = user.getUsername();
        String user_password = user.getPassword();
        if (usersList.containsKey(user_name)) {
            return usersList.get(user_name).equals(user_password);
        }
        return false;
    }
}
