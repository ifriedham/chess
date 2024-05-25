package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName, int gameID) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    GameData SaveGame(int gameID, GameData game) throws DataAccessException;
    void removeAllGames() throws DataAccessException;
}
