package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Integer createGame(String authToken, String gameName) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    GameData SaveGame(int gameID, GameData game) throws DataAccessException;
    void removeAllGames() throws DataAccessException;
    boolean isEmpty() throws DataAccessException;
}
