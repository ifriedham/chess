package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


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

            // TODO: FIGURE OUT HOW TO TEST THIS
            facade.logout(authData.authToken());
        }

        @Test
        void badLogout() throws Exception {
            Assertions.assertThrows(IOException.class, () -> facade.logout("badAuthToken"));
        }
    }



    AuthData registerSetup(String username, String password, String email) throws IOException {
        return facade.register(username, password, email);
    }

    AuthData loginSetup(String username, String password) throws IOException {
        return facade.login(username, password);
    }


}
