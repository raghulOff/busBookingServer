package com.example.auth.service;

import com.example.auth.dao.UserDAO;
import com.example.auth.dto.UserDTO;
import com.example.auth.model.User;
import com.example.auth.security.JwtUtil;
import com.example.auth.security.PasswordUtil;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

public class LoginService {
    public static Response loginVerification(User userInput) {
        User user = UserDAO.getUser(userInput.getUsername());
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        boolean matchPassword = PasswordUtil.verifyPassword(userInput.getPassword(), user.getPassword());
        if (!matchPassword) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }

        String token = JwtUtil.generateToken(user);

        NewCookie tokenCookie = new NewCookie("token", token, "/", null, null, -1, false, true);

        UserDTO userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());
        return Response.ok("Login successful").entity(userdto).cookie(tokenCookie).build();
    }
}
