package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    public GameData createGame(AuthData authData, String gameName) throws DataAccessException {
        // check if given authData is valid
        if (authData == null || authDAO.getAuth(authData.authToken()) == null) {
            throw new DataAccessException("unauthorized");
        }

        // check if game with gameID already exists


        return null;
    }

    public HashMap<Integer, GameData> listGames(AuthData authData) {
        return null;
    }

    public GameData joinGame(AuthData authData, String playerColor, int gameID) {
        return null;
    }

}
