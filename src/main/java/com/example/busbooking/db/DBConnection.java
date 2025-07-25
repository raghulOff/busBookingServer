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


//
//package com.example.busbooking.db;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.ResourceBundle;
//
//public class DBConnection {
//
//    // Singleton HikariDataSource instance
//    private static final HikariDataSource dataSource;
//
//    static {
//        ResourceBundle rd = ResourceBundle.getBundle("app");
//
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl(rd.getString("db.url"));
//        config.setUsername(rd.getString("db.user"));
//        config.setPassword(rd.getString("db.password"));
//        config.setDriverClassName(rd.getString("db.driver"));
//
//        // Optional tuning (good defaults, tweak based on app load)
//        config.setMaximumPoolSize(10);             // Max 10 connections in pool
//        config.setMinimumIdle(2);                  // Keep 2 idle
//        config.setIdleTimeout(300000);             // 5 mins
//        config.setConnectionTimeout(30000);        // Wait max 30s for a connection
//        config.setLeakDetectionThreshold(60000);   // Warn if held > 60s
//
//        dataSource = new HikariDataSource(config);
//    }
//
//    public static Connection getConnection() throws SQLException {
//        return dataSource.getConnection();  // Pooled connection
//    }
//}
