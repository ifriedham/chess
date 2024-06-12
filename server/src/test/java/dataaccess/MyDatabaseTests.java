package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;


import static org.junit.jupiter.api.Assertions.*;

public class MyDatabaseTests {
    SQLUserDAO userDAO;
    SQLGameDAO gameDAO;
    SQLAuthDAO authDAO;

    @BeforeEach
    void setUp() throws SQLException, DataAccessException {
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();
        authDAO = new SQLAuthDAO();

        userDAO.removeAllUsers();
        gameDAO.removeAllGames();
        authDAO.removeAllAuthTokens();
    }

    @Nested
    class TestUserMethods {

        @Nested
        class CreateUserTests {
            @Test
            public void testCreateUserSuccess() throws DataAccessException, SQLException {
                // create and store user
                UserData newUser = new UserData("testUser", "testPassword", "testEmail");
                userDAO.createUser(newUser);

                // directly query the database for the user
                try (Connection conn = DatabaseManager.getConnection()) {
                    try (var preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
                        preparedStatement.setString(1, newUser.username());
                        try (var rs = preparedStatement.executeQuery()) {
                            assertTrue(rs.next(), "User was not created");
                            assertEquals(newUser.username(), rs.getString("username"), "Username does not match");
                            assertEquals(newUser.email(), rs.getString("email"), "Email does not match");
                        }
                    }
                }
            }

            @Test
            public void testCreateUserFailure() throws DataAccessException, SQLException {
                // create and store user
                UserData newUser = new UserData("testUser", "testPassword", "testEmail");
                userDAO.createUser(newUser);

                // attempt to create the same user again
                assertThrows(DataAccessException.class, () -> userDAO.createUser(newUser));
            }
        }

        @Nested
        class GetUserTests {
            @Test
            public void testGetUserSuccess() throws DataAccessException, SQLException {
                // create and store user
                UserData newUser = new UserData("testUser", "testPassword", "testEmail");
                userDAO.createUser(newUser);

                // retrieve the user
                UserData retrievedUser = userDAO.getUser(newUser.username());

                // check that the retrieved user matches the created user
                assertEquals(newUser.username(), retrievedUser.username(), "Username does not match");
                assertEquals(newUser.email(), retrievedUser.email(), "Email does not match");
            }

            @Test
            public void testGetUserFailure() {
                // attempt to retrieve a non-existent user
                assertThrows(DataAccessException.class, () -> userDAO.getUser("nonexistentUser"));
            }
        }
    }

    @ Nested
    class TestGameMethods {
        @Nested
        class CreateGameTests {

            @Test
            public void testCreateGameSuccess() throws DataAccessException, SQLException {
                // create and store game
                gameDAO.createGame("testGame", 1);

                // directly query the database for the game called testGame
                try (Connection conn = DatabaseManager.getConnection()) {
                    try (var preparedStatement = conn.prepareStatement("SELECT * FROM games WHERE gameName = ?")) {
                        preparedStatement.setString(1, "testGame");
                        try (var rs = preparedStatement.executeQuery()) {
                            assertTrue(rs.next(), "Game was not created");
                            assertEquals("testGame", rs.getString("gameName"), "Game name does not match");
                        }
                    }
                }
            }

            @Test
            public void testCreateGameFailure() throws DataAccessException, SQLException {
                // create and store a game
                gameDAO.createGame("testGame", 1);

                // attempt to create the same game again
                assertThrows(DataAccessException.class, () -> gameDAO.createGame("testGame", 1));
            }
        }

        @Nested
        class GetGameTests {

            @Test
            public void testGetGameSuccess() throws DataAccessException, SQLException {
                // create and store a game
                GameData newGame = new GameData(1, null, null, "testGame", new ChessGame());
                gameDAO.createGame(newGame.gameName(), newGame.gameID());

                // attempt to get game from db
                GameData savedGame = gameDAO.getGame(newGame.gameID());
                assertEquals(newGame.gameName(), savedGame.gameName(), "GetGame failure");
            }

            @Test
            public void testGetGameFailure() throws DataAccessException, SQLException {
                // attempt to get a non-existent game
                GameData badGame = gameDAO.getGame(3);
                assertNull(badGame, "returned null");
            }
        }

        @Nested
        class ListGamesTests {

            @Test
            public void testListGamesSuccess() throws DataAccessException, SQLException {
                // create and store three games
                GameData game1 = new GameData(1, null, null, "testGame1", new ChessGame());
                GameData game2 = new GameData(2, null, null, "testGame2", new ChessGame());
                GameData game3 = new GameData(3, null, null, "testGame3", new ChessGame());

                gameDAO.createGame(game1.gameName(), game1.gameID());
                gameDAO.createGame(game2.gameName(), game2.gameID());
                gameDAO.createGame(game3.gameName(), game3.gameID());

                // create the expected list of games
                Collection<GameData> expectedGames = new ArrayList<>();
                expectedGames.add(game1);
                expectedGames.add(game2);
                expectedGames.add(game3);

                // check that the retrieved list matches the expected list
                assertEquals(expectedGames.size(), gameDAO.listGames().size(), "List of games does not match expected list");
            }

            @Test
            public void testListGamesFailure() throws DataAccessException, SQLException {
                // clear the games table
                gameDAO.removeAllGames();

                // attempt to list games
                Collection<GameData> retrievedGames = gameDAO.listGames();

                // check that the retrieved list is empty
                assertTrue(retrievedGames.isEmpty(), "List of games is not empty");
            }
        }

        @Nested
        class SaveGameTests {

            @Test
            public void testSaveGameSuccess() throws DataAccessException, SQLException {
                // create and store game
                GameData game = new GameData(1, null, null, "testGame", new ChessGame());
                gameDAO.createGame(game.gameName(), game.gameID());

                // create a modified game with same gameID and name + new players
                GameData newGame = new GameData(1, "testWhite", "testBlack", "testGame", new ChessGame());

                // attempt to save the game
                gameDAO.saveGame(newGame.gameID(), newGame);

                // check that the retrieved game matches the saved game
                assertEquals(newGame.gameName(), gameDAO.getGame(newGame.gameID()).gameName(), "Game name does not match");
            }

            @Test
            public void testSaveGameFailure() {
                // attempt to save non-existent game
                GameData badGame = new GameData(9999, null, null, "badGame", new ChessGame());
                assertThrows(DataAccessException.class, () -> gameDAO.saveGame(badGame.gameID(), badGame));
            }
        }
    }

    @Nested
    class TestAuthMethods {


        @Nested
        class CreateAuthTests {
            @Test
            public void testCreateAuthSuccess() throws DataAccessException, SQLException {
                // create and store auth
                String token = authDAO.createAuth("testUser");

                // check if an auth token is returned
                assertNotNull(token, "Auth token was not created");
            }

            // No need for negative test case, as createAuth() is only called internally,
            // having already been tested for bad input in UserService.java
        }

        @Nested
        class GetAuthTests {
            @Test
            public void testGetAuthSuccess() throws DataAccessException, SQLException {
                // create and store auth
                String token = authDAO.createAuth("testUser");

                // retrieve the auth
                String retrievedAuth = authDAO.getAuth(token);

                // check that the retrieved auth matches the created auth
                assertEquals(token, retrievedAuth, "auths do not match");
            }

            @Test
            public void testGetAuthFailure() throws DataAccessException, SQLException {
                // attempt to retrieve a non-existent auth
                String badAuth = authDAO.getAuth("bad token");
                assertNull(badAuth, "returned null");

            }
        }

        @Nested
        class GetUsernameTests {
            @Test
            public void testGetUsernameSuccess() throws DataAccessException, SQLException {
                // create and store auth
                String token = authDAO.createAuth("testUser");

                // retrieve the username
                String retrievedUsername = authDAO.getUsername(token);

                // check that the retrieved username matches the created username
                assertEquals("testUser", retrievedUsername, "Username does not match");
            }

            @Test
            public void testGetUsernameFailure() throws DataAccessException, SQLException {
                // attempt to retrieve a non-existent username
                String badUsername = authDAO.getUsername("nonexistentUser");
                assertNull(badUsername, "returned null");
            }
        }

        @Test
        public void testDeleteAuth() throws DataAccessException, SQLException {
            // create and store auth
            String token = authDAO.createAuth("testUser");

            // delete the auth
            authDAO.deleteAuth(token);

            // attempt to retrieve the deleted auth
            String badAuth = authDAO.getAuth(token);
            assertNull(badAuth, "returned null");
        }
    }

    @Test
    public void testClear() throws DataAccessException, SQLException {

        // populate tables
        userDAO.createUser(new UserData("testUser", "testPassword", "testEmail"));
        gameDAO.createGame("testGame", 1);
        authDAO.createAuth("testUser");

        // clears dbs here
        userDAO.removeAllUsers();
        gameDAO.removeAllGames();
        authDAO.removeAllAuthTokens();

        // check if the users table is empty
        assertTrue(userDAO.isEmpty(), "Users table is not empty");
        assertTrue(gameDAO.isEmpty(), "Games table is not empty");
        assertTrue(authDAO.isEmpty(), "Auths table is not empty");
    }
}