package server;

import com.google.gson.Gson;
import model.*;
import service.*;
import spark.*;
import dataaccess.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public class Server {
    private final UserService userService = new UserService();
    private final GameService gameService = new GameService();
    private final ClearService clearService = new ClearService();

    public static void main(String[] args) {
        Server server = new Server();
        server.run(Integer.parseInt(args[0]));
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Initialize the database
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

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
            var body = serializer.toJson(Map.of("username", result.username(), "authToken", result.authToken()));
            res.body(body);
            return body;
        } catch (DataAccessException e) {
            return errorHandler(e, res);
        }
    }

    private Object login(Request req, Response res) {
        try {
            var serializer = new Gson();
            UserData request = serializer.fromJson(req.body(), UserData.class);

            AuthData result = userService.login(request);

            res.status(200);
            var body = serializer.toJson(Map.of("username", result.username(), "authToken", result.authToken()));
            res.body(body);
            return body;
        } catch (DataAccessException e) {
            return errorHandler(e, res);
        }
    }

    private Object logout(Request req, Response res) {
        try {
            String request = req.headers("Authorization");

            userService.logout(request);

            res.status(200);
            var body = "{}";
            res.body(body);
            return body;

        } catch (DataAccessException e) {
            return errorHandler(e, res);
        }
    }

    private Object listGames(Request req, Response res) {
        try {
            var serializer = new Gson();
            String authToken = req.headers("Authorization");
            Collection<GameData> result = gameService.listGames(authToken);

            res.status(200);
            var body = serializer.toJson(Map.of("games", result));
            res.body(body);
            return body;

        } catch (DataAccessException e) {
            return errorHandler(e, res);
        }
    }

    private Object createGame(Request req, Response res) {
        try {
            var serializer = new Gson();
            String authToken = req.headers("Authorization");
            GameData request = serializer.fromJson(req.body(), GameData.class);
            String gameName = request.gameName();

            Integer result = gameService.createGame(authToken, gameName);

            res.status(200);
            var body = serializer.toJson(Map.of("gameID", result));
            res.body(body);
            return body;

        } catch (DataAccessException e) {
            return errorHandler(e, res);
        }
    }

    private Object joinGame(Request req, Response res) {
        try {
            var serializer = new Gson();
            var request = serializer.fromJson(req.body(), JoinRequest.class);
            String authToken = req.headers("Authorization");
            String playerColor = request.playerColor();
            Integer gameID = request.gameID();

            gameService.joinGame(authToken, playerColor, gameID);

            res.status(200);
            var body = "{}";
            res.body(body);
            return body;

        } catch (DataAccessException e) {
            return errorHandler(e, res);
        }
    }

    private Object clear(Request req, Response res) {
        try {
            clearService.clear();

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
        int statusCode = switch (errorMessage) {
            case "logout failed" -> 200;
            case "bad request" -> 400;
            case "unauthorized" -> 401;
            case "already taken" -> 403;
            default -> 500;
        };

        var body = new Gson().toJson(Map.of("message", "Error: " + errorMessage));
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
