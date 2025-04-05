package com.fairsharebu.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Generic DAO (Data Access Object) interface that defines common CRUD
 * operations.
 * 
 * @param <T> The type of the entity this DAO handles
 * @param <K> The type of the primary key of the entity
 */
public interface DAO<T, K> {

    /**
     * Insert a new entity into the database.
     * 
     * @param entity The entity to insert
     * @return The inserted entity with its ID populated
     * @throws SQLException If a database error occurs
     */
    T insert(T entity) throws SQLException;

    /**
     * Update an existing entity in the database.
     * 
     * @param entity The entity to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean update(T entity) throws SQLException;

    /**
     * Delete an entity from the database by its ID.
     * 
     * @param id The ID of the entity to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean delete(K id) throws SQLException;

    /**
     * Get an entity by its ID.
     * 
     * @param id The ID of the entity to retrieve
     * @return The retrieved entity, or null if not found
     * @throws SQLException If a database error occurs
     */
    T get(K id) throws SQLException;

    /**
     * Get all entities from the database.
     * 
     * @return A list of all entities
     * @throws SQLException If a database error occurs
     */
    List<T> getAll() throws SQLException;
}