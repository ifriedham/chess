package service;

import model.*;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import dataaccess.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class myAPITests {

    @Test
    public void testClearService() throws DataAccessException {
        // make test DAOs
        UserDAO users = new MemoryUserDAO();
        GameDAO games = new MemoryGameDAO();
        AuthDAO auths = new MemoryAuthDAO();

        // populate the test DAOs
        users.createUser(new UserData("testUser", "testPassword", "testEmail"));
        games.createGame("testGame", 1);
        auths.createAuth("testUsername");

        // create and call
        ClearService testClearService = new ClearService(users, games, auths);
        if (testClearService.clear() == null) {
            System.out.println("Data Cleared Successfully!");
        } else {
            System.out.println("Error: data not cleared.");
        }
    }

    @Nested
    class testRegister {
        @Test
        public void testIncorrectInput() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // run test here -> should throw exception "bad request"
            Exception exception = assertThrows(DataAccessException.class, () -> {
                userService.register(new UserData("no password giver", null, "IhatePasswords@live.com"));
            });
            assertEquals("bad request", exception.getMessage());
        }

        @Test
        public void testUsernameTaken() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "copycat"
            users.createUser(new UserData("copycat", "testPassword", "testEmail"));

            // run test here -> should throw exception "already taken"
            UserService userService = new UserService(auths, users);
            Exception exception = assertThrows(DataAccessException.class, () -> {
                userService.register(new UserData("copycat", "testPassword", "testEmail"));
            });
            assertEquals("already taken", exception.getMessage());        }

        @Test
        public void testRegisterSuccess() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // run test here -> should return an AuthData object
            AuthData testAuthData = userService.register(new UserData("testUser", "testPassword", "testEmail"));
            System.out.println("AuthData: " + testAuthData);
        }

    }

    @Nested
    class testLogin {
        @Test
        public void testBadPassword() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "testUser"
            users.createUser(new UserData("testUser", "testPassword", "testEmail"));

            // run test here -> should throw exception "unauthorized"
            UserService userService = new UserService(auths, users);
            userService.login(new UserData("testUser", "wrongPassword", "testEmail"));
        }

        @Test
        public void testBadUsername() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "testUser"
            users.createUser(new UserData("testUser", "testPassword", "testEmail"));

            // run test here -> should throw exception "unauthorized"
            UserService userService = new UserService(auths, users);
            userService.login(new UserData("badUsername", "testPassword", "testEmail"));
        }

        @Test
        public void testEmptyFields() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "testUser"
            users.createUser(new UserData("testUser", "testPassword", "testEmail"));

            // run test here -> should throw exception "must fill all fields"
            UserService userService = new UserService(auths, users);
            userService.login(new UserData("testUser", null, "testEmail"));
        }

        @Test
        public void testLoginSuccess() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "testUser"
            users.createUser(new UserData("testUser", "testPassword", "testEmail"));

            // run test here -> should return an AuthData object
            UserService userService = new UserService(auths, users);
            AuthData testAuthData = userService.login(new UserData("testUser", "testPassword", "testEmail"));
            System.out.println("AuthData: " + testAuthData);
        }

    }

    @Nested
    class testLogout{
        @Test
        public void testLogoutSuccess() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user
            AuthData testToken = userService.login(testUser);

            // run test here -> should print "Logout Successful!"
            if (userService.logout(testToken)) System.out.println("Logout Successful!");
        }

        @Test
        public void testBadAuth() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user
            AuthData testToken = userService.login(testUser);

            // run test here -> should print "unauthorized"
            userService.logout(new AuthData("badToken", "testUser"));
        }
    }

}
