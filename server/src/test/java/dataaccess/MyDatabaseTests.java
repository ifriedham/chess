package dataaccess;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Connection;


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
                assertThrows(SQLException.class, () -> userDAO.createUser(newUser));
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
            public void testGetUserFailure() throws DataAccessException, SQLException {
                // attempt to retrieve a non-existent user
                assertThrows(SQLException.class, () -> userDAO.getUser("nonexistentUser"));
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

            }
        }

        @Nested
        class GetGameTests {

            @Test
            public void testGetGameSuccess() throws DataAccessException, SQLException {

            }

            @Test
            public void testGetGameFailure() throws DataAccessException, SQLException {

            }
        }

        @Nested
        class ListGamesTests {

            @Test
            public void testListGamesSuccess() throws DataAccessException, SQLException {

            }

            @Test
            public void testListGamesFailure() throws DataAccessException, SQLException {

            }
        }

        @Nested
        class SaveGameTests {

            @Test
            public void testSaveGameSuccess() throws DataAccessException, SQLException {

            }

            @Test
            public void testSaveGameFailure() throws DataAccessException, SQLException {

            }
        }
    }

    @Nested
    class TestAuthMethods {

        @Nested
        class CreateAuthTests {

            @Test
            public void testCreateAuthSuccess() {

            }

            @Test
            public void testCreateAuthFailure() {

            }
        }

        @Nested
        class GetAuthTests {

            @Test
            public void testGetAuthSuccess() {

            }

            @Test
            public void testGetAuthFailure() {

            }
        }

        @Nested
        class GetUsernameTests {

            @Test
            public void testGetUsernameSuccess() {

            }

            @Test
            public void testGetUsernameFailure() {

            }
        }

        @Test
        public void testDeleteAuth() {

        }

        @Test
        public void testIsEmpty() {

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