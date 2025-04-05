package com.fairsharebu.dao;

import com.fairsharebu.model.Group;
import com.fairsharebu.model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Group-related database operations
 */
public interface GroupDAO extends DAO<Group, Integer> {

    /**
     * Creates a new group in the database
     * 
     * @param group The group to be created
     * @return The created group with the ID set
     * @throws SQLException If a database error occurs
     */
    Group createGroup(Group group) throws SQLException;

    /**
     * Updates an existing group in the database
     * 
     * @param group The group to be updated
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean updateGroup(Group group) throws SQLException;

    /**
     * Deletes a group from the database
     * 
     * @param groupId The ID of the group to be deleted
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean deleteGroup(int groupId) throws SQLException;

    /**
     * Gets a group by its ID
     * 
     * @param groupId The ID of the group to be retrieved
     * @return The group with the specified ID, or null if not found
     * @throws SQLException If a database error occurs
     */
    Group getGroupById(int groupId) throws SQLException;

    /**
     * Gets all groups created by a specific user
     * 
     * @param userId The ID of the user
     * @return A list of groups created by the user
     * @throws SQLException If a database error occurs
     */
    List<Group> getGroupsByCreator(int userId) throws SQLException;

    /**
     * Gets all groups that a user is a member of
     * 
     * @param userId The ID of the user
     * @return A list of groups the user is a member of
     * @throws SQLException If a database error occurs
     */
    List<Group> getGroupsByMember(int userId) throws SQLException;

    /**
     * Adds a user to a group
     * 
     * @param groupId The ID of the group
     * @param userId  The ID of the user to be added
     * @return true if the user was added successfully, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean addUserToGroup(int groupId, int userId) throws SQLException;

    /**
     * Removes a user from a group
     * 
     * @param groupId The ID of the group
     * @param userId  The ID of the user to be removed
     * @return true if the user was removed successfully, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean removeUserFromGroup(int groupId, int userId) throws SQLException;

    /**
     * Gets all members of a group
     * 
     * @param groupId The ID of the group
     * @return A list of users who are members of the group
     * @throws SQLException If a database error occurs
     */
    List<User> getGroupMembers(int groupId) throws SQLException;

    /**
     * Checks if a user is a member of a group
     * 
     * @param userId  The ID of the user
     * @param groupId The ID of the group
     * @return true if the user is a member of the group, false otherwise
     * @throws SQLException If a database error occurs
     */
    boolean isUserMemberOfGroup(int userId, int groupId) throws SQLException;
}