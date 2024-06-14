package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;

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
            AuthData authData = facade.register("testPlayer", "testPassword", "test@email.com");
            Assertions.assertTrue(authData.authToken().length() > 10);
        }

        @Test
        void badRegister() throws Exception {
            Assertions.assertThrows(IOException.class, () -> facade.register(null, "testPassword", "test@email.com"));
        }


    }


}
