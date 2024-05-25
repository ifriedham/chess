package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GameData> listGames() throws DataAccessException;
    void createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void SaveGame(int gameID, GameData game) throws DataAccessException;
    void removeAllGames() throws DataAccessException;
}
