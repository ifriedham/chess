package service;

import dataaccess.*;

import java.sql.SQLException;

public class ClearService {
    private final UserDAO users = new SQLUserDAO();
    private final GameDAO games = new SQLGameDAO();
    private final AuthDAO auths = new SQLAuthDAO();


    public Object clear() throws DataAccessException {
        try {
            users.removeAllUsers();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        try {
            games.removeAllGames();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        try {
            auths.removeAllAuthTokens();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        if (clearStatus()) return null;
        else throw new DataAccessException("data not cleared.");
    }

    private boolean clearStatus() throws DataAccessException {
        try {
            return users.isEmpty() && games.isEmpty() && auths.isEmpty();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
