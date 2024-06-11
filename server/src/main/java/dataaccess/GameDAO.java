package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public interface GameDAO {
    Integer createGame(String gameName, Integer gameID) throws DataAccessException, SQLException;

    GameData getGame(Integer gameID) throws DataAccessException, SQLException;

    Collection<GameData> listGames() throws DataAccessException, SQLException;

    GameData saveGame(int gameID, GameData game) throws DataAccessException, SQLException;

    void removeAllGames() throws DataAccessException, SQLException;

    boolean isEmpty() throws DataAccessException, SQLException;
}
