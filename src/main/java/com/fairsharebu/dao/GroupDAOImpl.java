package com.fairsharebu.dao;

import com.fairsharebu.model.Group;
import com.fairsharebu.model.User;
import com.fairsharebu.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the GroupDAO interface for group-related database
 * operations
 */
public class GroupDAOImpl implements GroupDAO {

    private final Connection connection;
    private final UserDAO userDAO;

    public GroupDAOImpl() throws SQLException {
        this.connection = DatabaseUtil.getConnection();
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public Group insert(Group group) throws SQLException {
        return createGroup(group);
    }

    @Override
    public boolean update(Group group) throws SQLException {
        return updateGroup(group);
    }

    @Override
    public boolean delete(Integer groupId) throws SQLException {
        return deleteGroup(groupId);
    }

    @Override
    public Group get(Integer groupId) throws SQLException {
        return getGroupById(groupId);
    }

    @Override
    public List<Group> getAll() throws SQLException {
        List<Group> groups = new ArrayList<>();
        String query = "SELECT * FROM `groups`";

        try (PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                groups.add(extractGroupFromResultSet(rs));
            }
        }

        return groups;
    }

    @Override
    public Group createGroup(Group group) throws SQLException {
        String query = "INSERT INTO `groups` (name, description, creatorId, groupImage, location, privateGroup) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, group.getName());
            stmt.setString(2, group.getDescription());
            stmt.setInt(3, group.getCreator().getUserId());
            stmt.setString(4, group.getGroupImage());
            stmt.setString(5, group.getLocation());
            stmt.setBoolean(6, group.isPrivateGroup());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating group failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    group.setGroupId(generatedKeys.getInt(1));

                    // Add the creator as a member of the group
                    addUserToGroup(group.getGroupId(), group.getCreator().getUserId());
                } else {
                    throw new SQLException("Creating group failed, no ID obtained.");
                }
            }
        }

        return group;
    }

    @Override
    public boolean updateGroup(Group group) throws SQLException {
        String query = "UPDATE `groups` SET name = ?, description = ?, groupImage = ?, location = ?, privateGroup = ? WHERE groupId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, group.getName());
            stmt.setString(2, group.getDescription());
            stmt.setString(3, group.getGroupImage());
            stmt.setString(4, group.getLocation());
            stmt.setBoolean(5, group.isPrivateGroup());
            stmt.setInt(6, group.getGroupId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteGroup(int groupId) throws SQLException {
        // First delete all members from the group
        String deleteMembers = "DELETE FROM group_members WHERE groupId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(deleteMembers)) {
            stmt.setInt(1, groupId);
            stmt.executeUpdate();
        }

        // Then delete the group itself
        String deleteGroup = "DELETE FROM `groups` WHERE groupId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(deleteGroup)) {
            stmt.setInt(1, groupId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public Group getGroupById(int groupId) throws SQLException {
        String query = "SELECT * FROM `groups` WHERE groupId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractGroupFromResultSet(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Group> getGroupsByCreator(int userId) throws SQLException {
        List<Group> groups = new ArrayList<>();
        String query = "SELECT * FROM `groups` WHERE creatorId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    groups.add(extractGroupFromResultSet(rs));
                }
            }
        }

        return groups;
    }

    @Override
    public List<Group> getGroupsByMember(int userId) throws SQLException {
        List<Group> groups = new ArrayList<>();
        String query = "SELECT g.* FROM `groups` g " +
                "JOIN group_members gm ON g.groupId = gm.groupId " +
                "WHERE gm.userId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    groups.add(extractGroupFromResultSet(rs));
                }
            }
        }

        return groups;
    }

    @Override
    public boolean addUserToGroup(int groupId, int userId) throws SQLException {
        String query = "INSERT IGNORE INTO group_members (groupId, userId) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean removeUserFromGroup(int groupId, int userId) throws SQLException {
        String query = "DELETE FROM group_members WHERE groupId = ? AND userId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean isUserMemberOfGroup(int userId, int groupId) throws SQLException {
        String query = "SELECT 1 FROM group_members WHERE groupId = ? AND userId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if the user is a member, false otherwise
            }
        }
    }

    @Override
    public List<User> getGroupMembers(int groupId) throws SQLException {
        List<User> members = new ArrayList<>();
        String query = "SELECT u.* FROM users u " +
                "JOIN group_members gm ON u.userId = gm.userId " +
                "WHERE gm.groupId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("userId"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setFullName(rs.getString("fullName"));
                    user.setPhoneNumber(rs.getString("phoneNumber"));
                    user.setProfilePicture(rs.getString("profilePicture"));

                    // Convert java.sql.Timestamp to LocalDateTime
                    Timestamp timestamp = rs.getTimestamp("registrationDate");
                    if (timestamp != null) {
                        user.setRegistrationDate(timestamp.toLocalDateTime());
                    }

                    user.setActive(rs.getBoolean("isActive"));

                    members.add(user);
                }
            }
        }

        return members;
    }

    private Group extractGroupFromResultSet(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setGroupId(rs.getInt("groupId"));
        group.setName(rs.getString("name"));
        group.setDescription(rs.getString("description"));

        // Get the creator user from the database
        int creatorId = rs.getInt("creatorId");
        User creator = userDAO.get(creatorId);
        group.setCreator(creator);

        // Convert java.sql.Timestamp to LocalDateTime
        Timestamp timestamp = rs.getTimestamp("createdDate");
        if (timestamp != null) {
            group.setCreatedDate(timestamp.toLocalDateTime());
        }

        group.setGroupImage(rs.getString("groupImage"));
        group.setLocation(rs.getString("location"));

        // Set the privateGroup field
        try {
            group.setPrivateGroup(rs.getBoolean("privateGroup"));
        } catch (SQLException e) {
            // If the column doesn't exist, default to false
            group.setPrivateGroup(false);
        }

        // Load members for this group
        group.setMembers(getGroupMembers(group.getGroupId()));

        return group;
    }
}