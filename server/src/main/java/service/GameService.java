package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
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
        if (authData == null || authDAO.getAuth(authData.authToken()) == null) {
            throw new DataAccessException("unauthorized");
        }

        // get a game ID based off of the given game name
        Integer gameID = generateID(gameName);

        // check if game with gameID already exists
        if (gameDAO.getGame(gameID) != null) {
            throw new DataAccessException("game already exists");
        }

        // create game and store in database, return gameID
        return gameDAO.createGame(gameName, gameID);
    }



    public HashMap<Integer, GameData> listGames(AuthData authData) {
        return null;
    }

    public GameData joinGame(AuthData authData, String playerColor, int gameID) {
        return null;
    }

    private Integer generateID(String gameName) {
        Random generator = new Random(gameName.hashCode());
        return 1000 + generator.nextInt(9000);
    }
}
