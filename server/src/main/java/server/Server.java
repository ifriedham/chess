package server;
import com.google.gson.Gson;
import model.*;
import org.eclipse.jetty.server.Authentication;
import service.*;
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
        try {
            var serializer = new Gson();
            UserData request = serializer.fromJson(req.body(), UserData.class);
            AuthData result = userService.register(request);

            res.status(200);
            var body = serializer.toJson(Map.of("username", result.username(), "authToken", registerResult.authToken()));
            res.body(body);
            return body;
        }
        catch (DataAccessException e) {
            return errorHandler(e, res);
        }
    }

    private Object login(Request req, Response res) {
        try {
            var serializer = new Gson();
            UserData request = serializer.fromJson(req.body(), UserData.class);
            AuthData result = userService.login(request);

            res.status(200);
            var body = serializer.toJson(Map.of("username", result.username(), "authToken", registerResult.authToken()));
            res.body(body);
            return body;
        }
        catch (DataAccessException e) {
            return errorHandler(e, res);
        }
    }

    private Object logout(Request req, Response res) {
        try {
            var serializer = new Gson();
            AuthData request = serializer.fromJson(req.body(), AuthData.class);
            if (userService.logout(request)) {
                res.status(200);
                var body = "{}";
                res.body(body);
                return body;
            }
        }
        catch (DataAccessException e) {
            return errorHandler(e, res);
        }
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

    private Object clear(Request req, Response res) {
        try {
            var result = clearService.clear();

            res.status(200);
            res.body("{}");

            return "{}";
        } catch (DataAccessException e) {
            return errorHandler(e, res);
        }
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
