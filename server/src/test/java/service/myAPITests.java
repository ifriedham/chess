package service;

import model.*;
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

        // create and call the test ClearService
        ClearService testClearService = new ClearService(users, games, auths);
        if (testClearService.clear() == null) {
            System.out.println("Success!");
        }
    }

}
