package com.fairsharebu.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application lifecycle listener for initializing and cleaning up database
 * resources.
 * This ensures the connection pool is properly created at application startup
 * and closed at application shutdown, preventing connection leaks.
 */
@WebListener
public class DatabaseInitListener implements ServletContextListener {

    /**
     * Initialize the database connection pool when the application starts.
     * 
     * @param sce ServletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing database connection pool...");
        DatabaseUtil.initializeDataSource(sce.getServletContext());
        System.out.println("Database connection pool initialized successfully.");
    }

    /**
     * Close the database connection pool when the application shuts down.
     * 
     * @param sce ServletContextEvent
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Shutting down database connection pool...");
        DatabaseUtil.closeDataSource();
        System.out.println("Database connection pool closed successfully.");
    }
}