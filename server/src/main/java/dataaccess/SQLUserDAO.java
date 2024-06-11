package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{

    @Override
    public void createUser(UserData newUser) throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
                statement.setString(1, newUser.username());
                statement.setString(2, newUser.password()); // should be hashed
                statement.setString(3, newUser.email());

                statement.executeUpdate();
            }
        }
    }

    @Override
    public UserData getUser(String username) throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"), // hashed password
                                rs.getString("email")
                        );
                    } else {
                        throw new SQLException("User not found");
                    }
                }
            }
        }
    }

    @Override
    public void removeAllUsers() throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            // clear all data from users table
            //noinspection SqlWithoutWhere
            try (var statement = conn.prepareStatement("DELETE FROM users")) {
                statement.executeUpdate();
            }
        }
    }

    @Override
    public boolean isEmpty() throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT 1 FROM users LIMIT 1")) {
                try (var resultSet = preparedStatement.executeQuery()) { // should return false if there isn't a row in the table
                    return !resultSet.next();
                }
            }
        }
    }
}
