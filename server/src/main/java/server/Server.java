package server;
import com.google.gson.Gson;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;
import dataaccess.*;

import java.util.Map;

public class Server {
    private final UserDAO users = new MemoryUserDAO();
    private final GameDAO games = new MemoryGameDAO();
    private final AuthDAO auths = new MemoryAuthDAO();

    private final UserService userService = new UserService(auths, users);
    private final GameService gameService = new GameService(auths, games);
    private final ClearService clearService= new ClearService(users, games, auths);


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registration);    // registration
        Spark.post("/session", this::login);        // login
        Spark.delete("/session", this::logout);     // logout
        Spark.get("/game", this::listGames);        // list games
        Spark.post("/game", this::createGame);      // create game
        Spark.put("/game", this::joinGame);         // join game
        Spark.delete("/db", this::clear);           // clear


        Spark.awaitInitialization();
        return Spark.port();
    }


    /* handlers */
    private Object registration(Request req, Response res) {
        return null;
    }

    private Object login(Request req, Response res) {
        return null;
    }

    private Object logout(Request req, Response res) {
        return null;
    }

    private Object listGames(Request req, Response res) {
        return null;
    }

    private Object createGame(Request req, Response res) {
        return null;
    }

    private Object joinGame(Request req, Response res) {
        return null;
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        try {
            var result = clearService.clear();
            if (result == null) {
                res.status(200);
                res.body("{}");
                return "{}";
            }
        } catch (DataAccessException e) {
            return errorHandler(e, res);
        }
        return null;
    }

    /* end handlers*/

    public Object errorHandler(Exception e, Response res) {
        String errorMessage = e.getMessage();
        String responseMessage;
        int statusCode;

        switch (errorMessage) {
            case "data not cleared.":
                responseMessage = errorMessage;
                statusCode = 500;
                break;
            default:
                responseMessage = "Unknown error";
                statusCode = 500;
        }

        var body = new Gson().toJson(Map.of("message", "Error: " + responseMessage));
        res.type("application/json");
        res.status(statusCode);
        res.body(body);
        return body;
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
