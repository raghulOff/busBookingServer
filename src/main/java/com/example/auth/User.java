package com.example.auth;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
    private String username;
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User -> " + username + " " + password;
    }
}
