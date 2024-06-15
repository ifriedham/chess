import chess.*;
import ui.*;
import client.ServerFacade;
import server.Server;

import java.io.IOException;

public class ClearDB {
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        ServerFacade facade = new ServerFacade(port);
        facade.clear();
        System.out.println("server cleared" + port);
        server.stop();
    }
}