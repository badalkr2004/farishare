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
                            "jdbc:mysql://localhost:3306/fairsharebu?useSSL=false&allowPublicKeyRetrieval=true");
                    dataSource.setUsername("root");
                    dataSource.setPassword("mypass");

                    // Improved connection pool configuration
                    dataSource.setInitialSize(10);
                    dataSource.setMaxTotal(50);
                    dataSource.setMaxIdle(25);
                    dataSource.setMinIdle(10);
                    dataSource.setMaxWaitMillis(30000);

                    // Added auto-recovery settings
                    dataSource.setRemoveAbandonedOnBorrow(true);
                    dataSource.setRemoveAbandonedOnMaintenance(true);
                    dataSource.setRemoveAbandonedTimeout(60);
                    dataSource.setLogAbandoned(true);

                    // Added connection validation
                    dataSource.setTestOnBorrow(true);
                    dataSource.setValidationQuery("SELECT 1");
                    dataSource.setValidationQueryTimeout(5);

                    // Set a limit to prevent too many connections
                    dataSource.setDefaultAutoCommit(true);
                    dataSource.setEnableAutoCommitOnReturn(true);
                    dataSource.setFastFailValidation(true);
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