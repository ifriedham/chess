package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private HashMap<Integer, GameData> games;

    public MemoryGameDAO() {
        games = new HashMap<>();
    }

    public int createGame(String gameName, int gameID) {
        games.put(gameID, new GameData(gameID, null, null, gameName, null));

        if (games.containsKey(gameID)) return gameID; //success!
        else return -1; //failure :(
    }

    public GameData getGame(int gameID) {
        if (games != null) return games.get(gameID);
        else return null;
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public GameData SaveGame(int gameID, GameData game) throws DataAccessException {
        if (games.containsKey(gameID)) {
            games.put(gameID, game);
            return game;
        } else return null;
    }

    public void removeAllGames() {
        games.clear();
    }
}
