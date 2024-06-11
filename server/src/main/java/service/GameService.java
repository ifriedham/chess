package service;

import dataaccess.*;

import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;

public class GameService {
    private final AuthDAO authDAO = new SQLAuthDAO();
    private final GameDAO gameDAO = new SQLGameDAO();


    public Integer createGame(String authToken, String gameName) throws DataAccessException {
        // check if given authData is valid
        if (isValid(authToken)) throw new DataAccessException("unauthorized");

        if (gameName == null) throw new DataAccessException("bad request");

        // get a game ID based off of the given game name
        Integer gameID = generateID(gameName);

        // check if game with gameID already exists
        try {
            if (gameDAO.getGame(gameID) != null) {
                throw new DataAccessException("game already exists");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        // create game and store in database, return gameID
        try {
            return gameDAO.createGame(gameName, gameID);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        // check if given authData is valid
        if (isValid(authToken)) throw new DataAccessException("unauthorized");

        // return a list of games
        try {
            return gameDAO.listGames();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public GameData joinGame(String authToken, String playerColor, Integer gameID) throws DataAccessException {
        // check if given authData is valid
        if (isValid(authToken)) throw new DataAccessException("unauthorized");

        // get username from authToken
        String userName = null;
        try {
            userName = authDAO.getUsername(authToken);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        // check if game exists
        GameData game = null;
        try {
            game = gameDAO.getGame(gameID);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        if (game == null) throw new DataAccessException("bad request");

        // update the game with the new player
        GameData updatedGame = updateGame(game, userName, playerColor);

        // save updated game to database
        try {
            return gameDAO.saveGame(gameID, updatedGame);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private GameData updateGame(GameData game, String userName, String playerColor) throws DataAccessException {
        // check if playerColor is valid
        if (playerColor == null || (!playerColor.equals("WHITE") && !playerColor.equals("BLACK"))) {
            throw new DataAccessException("bad request");
        }

        // add white player
        if (Objects.equals(playerColor, "WHITE") && game.whiteUsername() == null) {
            return new GameData(game.gameID(), userName, game.blackUsername(), game.gameName(), game.game());
        }

        // add black player
        else if (Objects.equals(playerColor, "BLACK") && game.blackUsername() == null) {
            return new GameData(game.gameID(), game.whiteUsername(), userName, game.gameName(), game.game());
        }

        // the requested color is already taken
        else throw new DataAccessException("already taken");
    }

    private Integer generateID(String gameName) {
        Random generator = new Random(gameName.hashCode());
        return 1000 + generator.nextInt(9000);
    }

    private boolean isValid(String authToken) throws DataAccessException {
        try {
            return authToken == null || authDAO.getAuth(authToken) == null;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
