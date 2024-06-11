package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO{


    @Override
    public Integer createGame(String gameName, Integer gameID) throws DataAccessException {

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {
                statement.setInt(1, gameID);
                statement.setString(2, null);
                statement.setString(3, null);
                statement.setString(4, gameName);
                var gameJson = new Gson().toJson(new ChessGame()); // serialize a fresh game
                statement.setString(5, gameJson);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameID;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?")) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                                int id = rs.getInt("gameID");
                                String white = rs.getString("whiteUsername");
                                String black = rs.getString("blackUsername");
                                String name = rs.getString("gameName");
                                var gameJson = rs.getString("game");
                                var game = new Gson().fromJson(gameJson, ChessGame.class);

                        return new GameData(id, white, black, name, game);
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
