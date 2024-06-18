package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SQLGameDAO;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.*;
import websocket.commands.*;
import websocket.messages.Error;
import static spark.Spark.*;

import java.io.IOException;

@WebSocket
public class WSServer {

    GameService gameService = new GameService();
    SQLGameDAO gameDAO = new SQLGameDAO();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(session, message);
        }
    }

    public void connect(Session session, String message) throws IOException {
        Connect joinGameRequest = new Gson().fromJson(message, Connect.class);
        String authToken = joinGameRequest.getAuthString();
        int gameID = joinGameRequest.getGameID();
        String playerColor = joinGameRequest.getPlayerColor();

        try {
            // Check if the authToken is valid
            gameService.isValid(authToken);

            // Get the GameData for the game the user is trying to join
            GameData gameData = gameDAO.getGame(gameID);

            // Check if the user is allowed to join the game
            if (playerColor != null) {
                if ((playerColor.equals("WHITE") && gameData.whiteUsername() != null) ||
                    (playerColor.equals("BLACK") && gameData.blackUsername() != null)) {
                    // The user is not allowed to join the game, send an error message
                    Error errorResponse = new Error("This color is already taken.");
                    session.getRemote().sendString(new Gson().toJson(errorResponse));
                    return;
                }

                if ((playerColor.equals("WHITE") && gameData.whiteUsername() != null) ||
                    (playerColor.equals("BLACK") && gameData.blackUsername() != null)) {
                    // The user is not allowed to join the game, send an error message
                    Error errorResponse = new Error("This color is already taken.");
                    session.getRemote().sendString(new Gson().toJson(errorResponse));
                    return;
                }
            }



            gameService.joinGame(authToken, playerColor, gameID);

            // Update the game in the database
            gameDAO.saveGame(gameID, gameData);

            // Send a success message to the client
            String successResponse = "Successfully joined the game.";
            session.getRemote().sendString(successResponse);

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}