package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public Integer createGame(AuthData authData, String gameName) throws DataAccessException {
        // check if given authData is valid
        if (!isValid(authData)) throw new DataAccessException("unauthorized");

        if (gameName == null) throw new DataAccessException("bad request");

        // get a game ID based off of the given game name
        Integer gameID = generateID(gameName);

        // check if game with gameID already exists
        if (gameDAO.getGame(gameID) != null) {
            throw new DataAccessException("game already exists");
        }

        // create game and store in database, return gameID
        return gameDAO.createGame(gameName, gameID);
    }

    public Collection<GameData> listGames(AuthData authData) throws DataAccessException {
        // check if given authData is valid
        if (!isValid(authData)) throw new DataAccessException("unauthorized");

        // return a list of games
        return gameDAO.listGames();
    }

    public GameData joinGame(AuthData authData, String playerColor, Integer gameID) throws DataAccessException {
        // check if given authData is valid
        if (!isValid(authData)) throw new DataAccessException("unauthorized");

        // check if game exists
        GameData game = gameDAO.getGame(gameID);
        if (game == null) throw new DataAccessException("bad request");

        // update the game with the new player
        GameData updatedGame = updateGame(game, playerColor, authData.username());

        // save updated game to database
        return gameDAO.SaveGame(gameID, updatedGame);
    }

    private GameData updateGame(GameData game, String playerColor, String username) throws DataAccessException {
        // check if playerColor is valid
        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            throw new DataAccessException("bad request");
        }

        // add white player
        if (Objects.equals(playerColor, "WHITE") && game.whiteUsername() == null) {
            return new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        }

        // add black player
        else if (Objects.equals(playerColor, "BLACK") && game.blackUsername() == null) {
            return new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }

        // the requested color is already taken
        else throw new DataAccessException("already taken");
    }

    private Integer generateID(String gameName) {
        Random generator = new Random(gameName.hashCode());
        return 1000 + generator.nextInt(9000);
    }

    private boolean isValid(AuthData authData) throws DataAccessException {
        if (authData == null || authDAO.getAuth(authData.authToken()) == null) {
            return false;
        } else return true;
    }
}
