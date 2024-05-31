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
            Exception exception = assertThrows(DataAccessException.class, () -> {
                userService.login(new UserData("testUser", "wrongPassword", "testEmail"));
            });
            assertEquals("unauthorized", exception.getMessage());        }

        @Test
        public void testBadUsername() throws DataAccessException {
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "testUser"
            users.createUser(new UserData("testUser", "testPassword", "testEmail"));

            // run test here -> should throw exception "unauthorized"
            UserService userService = new UserService(auths, users);
            Exception exception = assertThrows(DataAccessException.class, () -> {
                userService.login(new UserData("testUser", "wrongPassword", "testEmail"));
            });
            assertEquals("unauthorized", exception.getMessage());
        }

        @Test
        public void testEmptyFields() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "testUser"
            users.createUser(new UserData("testUser", "testPassword", "testEmail"));

            // run test here -> should throw exception "must fill all fields"
            UserService userService = new UserService(auths, users);
            Exception exception = assertThrows(DataAccessException.class, () -> {
                userService.login(new UserData("testUser", null, "testEmail"));
            });
            assertEquals("must fill all fields", exception.getMessage());        }

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
            Exception exception = assertThrows(DataAccessException.class, () -> {
                userService.logout(new AuthData("badToken", "testUser"));
            });
            assertEquals("unauthorized", exception.getMessage());        }
    }

    @Nested
    class testCreateGame{
        @Test
        public void testCreateGameSuccess() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            GameDAO games = new MemoryGameDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user, get auth token back
            AuthData testToken = userService.login(testUser);

            // TEST RUN HERE -> create game, get gameID back
            GameService gameService = new GameService(auths, games);
            Integer gameID = -1;
            gameID = gameService.createGame(testToken, "testGame");

            if (gameID == -1) System.out.println("ERROR: Game not created");
            else System.out.println("SUCCESS! :: GameID: " + gameID);
        }

        @Test
        public void testGameAlreadyExists() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            GameDAO games = new MemoryGameDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user, get auth token back
            AuthData testToken = userService.login(testUser);

            GameService gameService = new GameService(auths, games);

            // create a game with the name "testGame"
            gameService.createGame(testToken, "testGame");

            // TEST HERE -> create duplicate game "testGame"
            Exception exception = assertThrows(DataAccessException.class, () -> {
                gameService.createGame(testToken, "testGame");
            });
            assertEquals("game already exists", exception.getMessage());
        }
    }

    @Nested
    class testListGame{
        @Test
        public void testListGameSuccess() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            GameDAO games = new MemoryGameDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user, get auth token back
            AuthData testToken = userService.login(testUser);

            GameService gameService = new GameService(auths, games);

            // create some games
            gameService.createGame(testToken, "testGame1");
            gameService.createGame(testToken, "testGame2");
            gameService.createGame(testToken, "testGame3");

            // TEST HERE -> list games
            System.out.println("Games: " + gameService.listGames(testToken));
        }

        @Test
        public void testListGameUnauthorized() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            GameDAO games = new MemoryGameDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user, get auth token back
            AuthData testToken = userService.login(testUser);

            GameService gameService = new GameService(auths, games);

            // create some games
            gameService.createGame(testToken, "testGame1");
            gameService.createGame(testToken, "testGame2");
            gameService.createGame(testToken, "testGame3");

            // TEST HERE -> list games with bad token
            Exception exception = assertThrows(DataAccessException.class, () -> {
                gameService.listGames(new AuthData("badToken", "testUser"));
            });
            assertEquals("unauthorized", exception.getMessage());
        }
    }

    @Nested
    class testJoinGame {
        @Test
        public void testJoinGameSuccess() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            GameDAO games = new MemoryGameDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user, get auth token back
            AuthData testToken = userService.login(testUser);

            GameService gameService = new GameService(auths, games);

            // create a game
            Integer gameID = gameService.createGame(testToken, "testGame");

            // TEST HERE -> join game
            GameData testGameData = gameService.joinGame(testToken, "WHITE", gameID);
            System.out.println("GameData: " + testGameData);
        }

        @Test
        public void testColorAlreadyTaken() throws DataAccessException{
            UserDAO users = new MemoryUserDAO();
            GameDAO games = new MemoryGameDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register two test users
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            UserData testUser2 = new UserData("testUser2", "testPassword2", "testEmail2");
            userService.register(testUser);
            userService.register(testUser2);

            // login the test users, get auth tokens back
            AuthData testToken = userService.login(testUser);
            AuthData TestToken2 = userService.login(testUser2);

            GameService gameService = new GameService(auths, games);

            // create a game, populate with a white player
            Integer gameID = gameService.createGame(testToken, "testGame");
            gameService.joinGame(testToken, "WHITE", gameID);

            // TEST HERE -> join game with another white player
            Exception exception = assertThrows(DataAccessException.class, () -> {
                gameService.joinGame(TestToken2, "WHITE", gameID);
            });
            assertEquals("already taken", exception.getMessage());
        };
    }
}
