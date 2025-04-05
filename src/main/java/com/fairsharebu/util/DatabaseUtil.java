package com.fairsharebu.util;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utility class for database connection management.
 * Uses Apache DBCP2 for connection pooling.
 */
public class DatabaseUtil {
    private static BasicDataSource dataSource;

    /**
     * Initialize the database connection pool.
     * Should be called when the application starts.
     * 
     * @param context ServletContext containing database configuration
     */
    public static void initializeDataSource(ServletContext context) {
        if (dataSource == null) {
            synchronized (DatabaseUtil.class) {
                if (dataSource == null) {
                    // String jdbcURL = context.getInitParameter("jdbcURL");
                    // String jdbcUsername = context.getInitParameter("jdbcUsername");
                    // String jdbcPassword = context.getInitParameter("jdbcPassword");

                    dataSource = new BasicDataSource();
                    dataSource.setUrl(
                            "jdbc:mysql://localhost:3306/fairsharebu?useSSL=false&amp;allowPublicKeyRetrieval=true");
                    dataSource.setUsername("fairshare");
                    dataSource.setPassword("aryan");

                    // Connection pool configuration
                    dataSource.setInitialSize(5);
                    dataSource.setMaxTotal(20);
                    dataSource.setMaxIdle(10);
                    dataSource.setMinIdle(5);
                    dataSource.setMaxWaitMillis(10000);

                    // Test connections before using them
                    dataSource.setTestOnBorrow(true);
                    dataSource.setValidationQuery("SELECT 1");
                }
            }
        }
    }

    /**
     * Get a connection from the connection pool.
     * 
     * @return A database connection
     * @throws SQLException If a database error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized. Call initializeDataSource first.");
        }
        return dataSource.getConnection();
    }

    /**
     * Close the connection pool.
     * Should be called when the application is shutting down.
     */
    public static void closeDataSource() {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
                // Log this exception
                e.printStackTrace();
            }
        }
    }
}