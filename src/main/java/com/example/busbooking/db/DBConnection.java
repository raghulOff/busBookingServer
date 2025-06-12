package com.example.busbooking.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;

public class DBConnection {
    //    private static final String password = System.getenv("DB_PASSWORD");
    //    Use this if password is set as environment variable;


    private static final ResourceBundle rd = ResourceBundle.getBundle("app");
    private static final String url = rd.getString("db.url");
    private static final String username = rd.getString("db.user");
    private static final String driver = rd.getString("db.driver");
    private static final String password = rd.getString("db.password");

    public static Connection getConnection() throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
}
