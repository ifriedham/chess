package dataaccess;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyDAOTests {
    private SQLUserDAO userDao = new SQLUserDAO();

    @Test
    public void testPopulateAndPrintUsers() throws SQLException, DataAccessException {
        // Create a couple of users
        UserData user1 = new UserData("user1", "password1", "user1@email.com");
        UserData user2 = new UserData("user2", "password2", "user2@email.com");

        // Insert the users into the database
        userDao.createUser(user1);
        userDao.createUser(user2);

        // Print out the users table
        printUsersTable();
    }

    private void printUsersTable() throws SQLException {
        try (Connection conn = getConnection()) {
            assert conn != null;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

                while (rs.next()) {
                    System.out.println("Username: " + rs.getString("username") + ", Password: " + rs.getString("password") + ", Email: " + rs.getString("email"));
                }
            }
        }
    }

    private Connection getConnection() {
        try {
            return DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}