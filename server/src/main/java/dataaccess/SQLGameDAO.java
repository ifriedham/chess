package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO{


    @Override
    public Integer createGame(String gameName, Integer gameID) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public GameData saveGame(int gameID, GameData game) throws DataAccessException {
        return null;
    }

    @Override
    public void removeAllGames() throws DataAccessException {

    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        return false;
    }
}
