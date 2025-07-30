package com.example.busbooking.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    /**
     * Rolls back the current database transaction if the connection is not null.
     *
     * @param conn the JDBC connection to roll back
     */

    public static void rollbackConnection(Connection conn) {

        if (conn != null) {
            try {
                conn.rollback();
            } catch (Exception rollbackEx) {
                System.out.println(rollbackEx.getMessage());
            }
        }
    }


    /**
     * Closes the DB connection
     * @param conn DB Connection
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Connection close failed: " + e);
            }
        }
    }


    /**
     * Closing a prepared statement
     */
    public static void closePreparedStatement( PreparedStatement preparedStatement ) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
