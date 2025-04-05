package com.fairsharebu.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application lifecycle listener implementation for initializing
 * the database connection pool on application startup.
 */
@WebListener
public class DatabaseInitListener implements ServletContextListener {

    /**
     * Initialize the database connection pool when the application starts.
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        DatabaseUtil.initializeDataSource(event.getServletContext());
        System.out.println("Database connection pool initialized.");
    }

    /**
     * Close the database connection pool when the application shuts down.
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        DatabaseUtil.closeDataSource();
        System.out.println("Database connection pool closed.");
    }
}