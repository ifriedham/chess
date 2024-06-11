package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO{


    @Override
    public Integer createGame(String gameName, Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {
                preparedStatement.setInt(1, gameID);
                preparedStatement.setString(2, null);
                preparedStatement.setString(3, null);
                preparedStatement.setString(4, gameName);
                var gameJson = new Gson().toJson(new ChessGame()); // serialize a fresh game
                preparedStatement.setString(5, gameJson);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameID;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM games WHERE gameID = ?")) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return getGameData(rs);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gameList = null;
        
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM games")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        gameList.add(getGameData(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return gameList;
    }

    @Override
    public GameData saveGame(int gameID, GameData game) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?")) {
                preparedStatement.setInt(1, gameID);
                preparedStatement.setString(2, game.whiteUsername());
                preparedStatement.setString(3, game.blackUsername());
                preparedStatement.setString(4, game.gameName());
                var gameJson = new Gson().toJson(game); // serialize game
                preparedStatement.setString(5, gameJson);

                preparedStatement.executeUpdate();
                return game;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void removeAllGames() throws DataAccessException {

    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        return false;
    }

    private GameData getGameData(ResultSet rs) throws SQLException {
        int id = rs.getInt("gameID");
        String white = rs.getString("whiteUsername");
        String black = rs.getString("blackUsername");
        String name = rs.getString("gameName");
        var gameJson = rs.getString("game");
        var game = new Gson().fromJson(gameJson, ChessGame.class);

        return new GameData(id, white, black, name, game);
    }
}
