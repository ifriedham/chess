package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO{
    @Override
    public String createAuth(String username) throws SQLException, DataAccessException {
        String token;
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO auths (authToken, username) VALUES (?, ?)")) {
                // create token from username
                token = UUID.randomUUID().toString();

                statement.setString(1, token);
                statement.setString(2, username);

                statement.executeUpdate();
            }
        }

        // return authToken
        return token;
    }

    @Override
    public String getAuth(String authToken) throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT authToken FROM auths WHERE authToken = ?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("authToken");
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    @Override
    public String getUsername(String authToken) throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT username FROM auths WHERE authToken = ?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auths WHERE authToken = ?")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public void removeAllAuthTokens() throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            // clear all data from auths table
            //noinspection SqlWithoutWhere
            try (var statement = conn.prepareStatement("DELETE FROM auths")) {
                statement.executeUpdate();
            }
        }
    }

    @Override
    public boolean isEmpty() throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT 1 FROM auths LIMIT 1")) {
                try (var resultSet = preparedStatement.executeQuery()) { // should return false if there isn't a row in the table
                    return !resultSet.next();
                }
            }
        }
    }
}
