package com.example.busbooking.dto;

public class UserDTO {
    private String username;
    private int userId;
    private int roleId;

    public UserDTO(String username, int userId, int roleId) {
        this.userId = userId;
        this.username = username;
        this.roleId = roleId;
    }
    public String getUsername() { return username; }
    public int getUserId() { return userId; }
    public int getRoleId() { return roleId; }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setUserId(int roleId) {
        this.roleId = roleId;
    }
    public void setRoleId(int userId) {
        this.userId = userId;
    }
}
