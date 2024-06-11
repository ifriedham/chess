package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public interface GameDAO {
    Integer createGame(String gameName, Integer gameID) throws DataAccessException;

    GameData getGame(Integer gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    GameData saveGame(int gameID, GameData game) throws DataAccessException;

    void removeAllGames() throws DataAccessException;

    boolean isEmpty() throws DataAccessException;
}
