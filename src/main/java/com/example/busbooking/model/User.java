package com.example.busbooking.model;
//
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement

public class User {
    private String username;
    private String password;
    private Role role;
    private int roleId;
    private int userId;

    public User() {
    }

    public User( String username, String password, Role role, int roleId, int userId ) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.roleId = roleId;
        this.userId = userId;
    }


    public void setUsername( String username ) {
        this.username = username;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public void setRoleId( int id ) {
        this.roleId = id;
    }

    public void setUserId( int id ) {
        this.userId = id;
    }

    public void setRole( Role role ) {
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

    public int getRoleId() {
        return roleId;
    }

    public int getUserId() {
        return userId;
    }


    @Override
    public String toString() {
        return "User -> " + username + " " + password + " " + role;
    }
}
