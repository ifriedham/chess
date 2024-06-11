package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyDAOTests {

    @Nested
    class TestRegister {
        @Test
        public void testCreateUsers() throws SQLException, DataAccessException {
            SQLUserDAO userDao = new SQLUserDAO();

            // Create multiple users
            UserData user1 = new UserData("user1", "password1", "user1@email.com");
            UserData user2 = new UserData("user2", "password2", "user2@email.com");

            // Insert users into the database
            userDao.createUser(user1);
            userDao.createUser(user2);

            // Print out users table (passwords hashed)
            printUsersTable();
        }

        @Test
        public void testBadRegistration() throws SQLException, DataAccessException {
            SQLUserDAO userDao = new SQLUserDAO();

            // Create user, add them to database
            UserData user = new UserData("user", "password", "user@email.com");
            userDao.createUser(user);

            // create and attempt to place duplicate user in db
            UserData duplicateUser = new UserData("user", "password", "user.email.com");
            userDao.createUser(duplicateUser);
        }


        private void printUsersTable() throws SQLException {

        }
    }
}