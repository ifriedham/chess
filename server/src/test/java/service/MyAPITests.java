/*
package service;

import model.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import dataaccess.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyAPITests {

    @Test
    public void testClearService() throws DataAccessException, SQLException {
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
    class TestRegister {
        @Test
        public void testIncorrectInput() throws DataAccessException {
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
        public void testUsernameTaken() throws DataAccessException, SQLException {
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "copycat"
            users.createUser(new UserData("copycat", "testPassword", "testEmail"));

            // run test here -> should throw exception "already taken"
            UserService userService = new UserService(auths, users);
            Exception exception = assertThrows(DataAccessException.class, () -> {
                userService.register(new UserData("copycat", "testPassword", "testEmail"));
            });
            assertEquals("already taken", exception.getMessage());
        }

        @Test
        public void testRegisterSuccess() throws DataAccessException {
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // run test here -> should return an AuthData object
            AuthData testAuthData = userService.register(new UserData("testUser", "testPassword", "testEmail"));
            System.out.println("AuthData: " + testAuthData);
        }

    }

    @Nested
    class TestLogin {
        @Test
        public void testBadPassword() throws DataAccessException, SQLException {
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "testUser"
            users.createUser(new UserData("testUser", hashPW("testPassword"), "testEmail"));

            // run test here -> should throw exception "unauthorized"
            UserService userService = new UserService(auths, users);
            Exception exception = assertThrows(DataAccessException.class, () -> {
                userService.login(new UserData("testUser", "wrongPassword", "testEmail"));
            });
            assertEquals("unauthorized", exception.getMessage());
        }

        @Test
        public void testBadUsername() throws DataAccessException, SQLException {
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "testUser"
            users.createUser(new UserData("testUser", hashPW("testPassword"), "testEmail"));

            // run test here -> should throw exception "unauthorized"
            UserService userService = new UserService(auths, users);
            Exception exception = assertThrows(DataAccessException.class, () -> {
                userService.login(new UserData("badUserName", "testPassword", "testEmail"));
            });
            assertEquals("unauthorized", exception.getMessage());
        }

        @Test
        public void testEmptyFields() throws DataAccessException, SQLException {
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "testUser"
            users.createUser(new UserData("testUser", hashPW("testPassword"), "testEmail"));

            // run test here -> should throw exception "must fill all fields"
            UserService userService = new UserService(auths, users);
            Exception exception = assertThrows(DataAccessException.class, () -> {
                userService.login(new UserData("testUser", null, "testEmail"));
            });
            assertEquals("must fill all fields", exception.getMessage());
        }

        @Test
        public void testLoginSuccess() throws DataAccessException, SQLException {
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();

            // store a user with the username "testUser"
            users.createUser(new UserData("testUser", hashPW("testPassword"), "testEmail"));

            // run test here -> should return an AuthData object
            UserService userService = new UserService(auths, users);
            AuthData testAuthData = userService.login(new UserData("testUser", "testPassword", "testEmail"));
            System.out.println("AuthData: " + testAuthData);
        }

    }

    private String hashPW(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Nested
    class TestLogout {
        @Test
        public void testLogoutSuccess() throws DataAccessException {
            UserDAO users = new MemoryUserDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user
            AuthData testToken = userService.login(testUser);


            // run test here -> should print "Logout Successful!"
            if (userService.logout(testToken.authToken())) System.out.println("Logout Successful!");
        }

        @Test
        public void testBadAuth() throws DataAccessException {
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
                userService.logout("badToken");
            });
            assertEquals("unauthorized", exception.getMessage());
        }
    }

    @Nested
    class TestCreateGame {
        @Test
        public void testCreateGameSuccess() throws DataAccessException {
            UserDAO users = new MemoryUserDAO();
            GameDAO games = new MemoryGameDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user, get auth token back
            AuthData testToken = userService.login(testUser);
            String token = testToken.authToken();

            // TEST RUN HERE -> create game, get gameID back
            GameService gameService = new GameService(auths, games);
            Integer gameID = -1;
            gameID = gameService.createGame(token, "testGame");

            if (gameID == -1) System.out.println("ERROR: Game not created");
            else System.out.println("SUCCESS! :: GameID: " + gameID);
        }

        @Test
        public void testGameAlreadyExists() throws DataAccessException {
            UserDAO users = new MemoryUserDAO();
            GameDAO games = new MemoryGameDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user, get auth token back
            AuthData testToken = userService.login(testUser);
            String token = testToken.authToken();


            GameService gameService = new GameService(auths, games);

            // create a game with the name "testGame"
            gameService.createGame(token, "testGame");

            // TEST HERE -> create duplicate game "testGame"
            Exception exception = assertThrows(DataAccessException.class, () -> {
                gameService.createGame(token, "testGame");
            });
            assertEquals("game already exists", exception.getMessage());
        }
    }

    @Nested
    class TestListGame {
        @Test
        public void testListGameSuccess() throws DataAccessException {
            UserDAO users = new MemoryUserDAO();
            GameDAO games = new MemoryGameDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user, get auth token back
            AuthData testToken = userService.login(testUser);
            String token = testToken.authToken();


            GameService gameService = new GameService(auths, games);

            // create some games
            gameService.createGame(token, "testGame1");
            gameService.createGame(token, "testGame2");
            gameService.createGame(token, "testGame3");

            // TEST HERE -> list games
            System.out.println("Games: " + gameService.listGames(token));
        }

        @Test
        public void testListGameUnauthorized() throws DataAccessException {
            UserDAO users = new MemoryUserDAO();
            GameDAO games = new MemoryGameDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user, get auth token back
            AuthData testToken = userService.login(testUser);
            String token = testToken.authToken();


            GameService gameService = new GameService(auths, games);

            // create some games
            gameService.createGame(token, "testGame1");
            gameService.createGame(token, "testGame2");
            gameService.createGame(token, "testGame3");

            // TEST HERE -> list games with bad token
            Exception exception = assertThrows(DataAccessException.class, () -> {
                gameService.listGames("badToken");
            });
            assertEquals("unauthorized", exception.getMessage());
        }
    }

    @Nested
    class TestJoinGame {
        @Test
        public void testJoinGameSuccess() throws DataAccessException {
            UserDAO users = new MemoryUserDAO();
            GameDAO games = new MemoryGameDAO();
            AuthDAO auths = new MemoryAuthDAO();
            UserService userService = new UserService(auths, users);

            // create and register a test user
            UserData testUser = new UserData("testUser", "testPassword", "testEmail");
            userService.register(testUser);

            // login the test user, get auth token back
            AuthData testToken = userService.login(testUser);
            String token = testToken.authToken();


            GameService gameService = new GameService(auths, games);

            // create a game
            Integer gameID = gameService.createGame(token, "testGame");

            // TEST HERE -> join game
            GameData testGameData = gameService.joinGame(token,"WHITE",gameID);
            System.out.println("GameData: " + testGameData);
        }

        @Test
        public void testColorAlreadyTaken() throws DataAccessException {
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
            String token = testToken.authToken();

            AuthData testToken2 = userService.login(testUser2);
            String token2 = testToken2.authToken();


            GameService gameService = new GameService(auths, games);

            // create a game, populate with a white player
            Integer gameID = gameService.createGame(token, "testGame");
            gameService.joinGame(token, "WHITE", gameID);

            // TEST HERE -> join game with another white player
            Exception exception = assertThrows(DataAccessException.class, () -> {
                gameService.joinGame(token2, "WHITE", gameID);
            });
            assertEquals("already taken", exception.getMessage());
        }

        ;
    }
}
*/
