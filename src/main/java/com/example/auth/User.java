package com.example.auth;

import jakarta.xml.bind.annotation.XmlRootElement;
@XmlRootElement

public class User {
    private String username;
    private String password;
    private Role role;

    public User() {}
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }


    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public Role getRole() {
        return role;
    }


    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isDeveloper() {
        return this.role == Role.DEVELOPER;
    }

    public boolean isUser() {
        return this.role == Role.USER;
    }



    @Override
    public String toString() {
        return "User -> " + username + " " + password + " " + role;
    }
}
