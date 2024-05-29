package service;

import model.*;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import dataaccess.*;

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
            userService.register(new UserData("no password giver", null, "IhatePasswords@live.com"));
        }

        @Test
        public void testUsernameTaken() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "copycat"
            users.createUser(new UserData("copycat", "testPassword", "testEmail"));

            // run test here -> should throw exception "already taken"
            UserService userService = new UserService(auths, users);
            userService.register(new UserData("copycat", "testPassword", "testEmail"));
        }

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

}
