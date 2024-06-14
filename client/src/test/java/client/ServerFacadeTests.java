package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clear() throws IOException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Nested
    class RegisterTests {

        @Test
        void goodRegister() throws Exception {
            AuthData authData = registerSetup("testUser", "testPassword", "testEmail");
            Assertions.assertTrue(authData.authToken().length() > 10);
        }

        @Test
        void badRegister() throws Exception {
            Assertions.assertThrows(IOException.class, () -> registerSetup(null, "testPassword", "badEmail"));
        }
    }

    @Nested
    class LoginTests {

        @Test
        void goodLogin() throws Exception {
            AuthData authData = registerSetup("testUser", "testPassword", "testEmail");

            AuthData loginData = facade.login("testUser", "testPassword");
            Assertions.assertEquals(authData.username(), loginData.username());
        }

        @Test
        void badLogin() throws Exception {
            registerSetup("testUser", "testPassword", "testEmail");

            Assertions.assertThrows(IOException.class, () -> facade.login("testUser", "badPassword"));
        }
    }

    @Nested
    class LogoutTests {

        @Test
        void goodLogout() throws Exception {
            AuthData authData = registerSetup("testUser", "testPassword", "testEmail");
            AuthData loginData = loginSetup("testUser", "testPassword");

            int responseCode = facade.logout(loginData.authToken());
            assertEquals(200, responseCode);
        }

        @Test
        void badLogout() throws Exception {
            Assertions.assertThrows(IOException.class, () -> facade.logout("badAuthToken"));
        }
    }

    @Nested
    class ListGamesTests {

        @Test
        void goodListGames() throws Exception {

        }

        @Test
        void badListGames() throws Exception {

        }
    }

    @Nested
    class CreateGameTests {

        @Test
        void goodCreateGame() throws Exception {
            AuthData authData = registerSetup("testUser", "testPassword", "testEmail");
            AuthData loginData = loginSetup("testUser", "testPassword");

            int id = -1;
            id = facade.createGame(loginData.authToken(), "testGame");
            assertTrue(id > 0);
        }

        @Test
        void badCreateGame() throws Exception {
            AuthData authData = registerSetup("testUser", "testPassword", "testEmail");
            AuthData loginData = loginSetup("testUser", "testPassword");

            Assertions.assertThrows(IOException.class, () -> facade.createGame("badAuthToken", "testGame"));
        }
    }

    @Nested
    class JoinGameTests {

        @Test
        void goodJoinGame() throws Exception {
            registerSetup("testUser", "testPassword", "testEmail");
            AuthData loginData = loginSetup("testUser", "testPassword");
            Integer gameID = facade.createGame(loginData.authToken(), "testGame");

            int responseCode = facade.joinGame(loginData.authToken(), "WHITE", gameID);
            assertEquals(200, responseCode);
        }

        @Test
        void badJoinGame() throws Exception {
            registerSetup("whiteUser", "whitePassword", "white@email.com");
            AuthData whiteAuth = loginSetup("whiteUser", "whitePassword");
            Integer gameID = facade.createGame(whiteAuth.authToken(), "testGame");

            // join whiteUser as white
            facade.joinGame(whiteAuth.authToken(), "WHITE", gameID);
            facade.logout(whiteAuth.authToken());

            // create black user, attempt to join as white
            registerSetup("blackUser", "blackPassword", "black@email.com");
            AuthData blackAuth = loginSetup("blackUser", "blackPassword");
            Assertions.assertThrows(IOException.class, () -> facade.joinGame(blackAuth.authToken(), "WHITE", gameID));
        }
    }


    AuthData registerSetup(String username, String password, String email) throws IOException {
        AuthData auth =  facade.register(username, password, email);
        Assertions.assertNotNull(auth, "Registration failed");
        return auth;
    }

    AuthData loginSetup(String username, String password) throws IOException {
        AuthData loginData = facade.login(username, password);
        assertNotNull(loginData, "Login failed");
        return loginData;
    }


}
