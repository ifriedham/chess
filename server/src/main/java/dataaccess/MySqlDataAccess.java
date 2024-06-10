package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlDataAccess extends DatabaseManager {
    public MySqlDataAccess() throws DataAccessException, SQLException {
        initializeUsers();
        initializeGames();
        initializeAuths();
    }

    private void initializeUsers() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()){
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT NOT NULL PRIMARY KEY, " +
                    "username VARCHAR(255), " +
                    "password VARCHAR(255), " +
                    "email VARCHAR(255)" +
                    ")";

            try (Statement statement = conn.createStatement()) {
                statement.execute(sql);
            }

        } catch (SQLException e) {
            throw new DataAccessException("unable to create users table");
        }

    }

    private void initializeGames() {
    }

    private void initializeAuths() {
    }


}
