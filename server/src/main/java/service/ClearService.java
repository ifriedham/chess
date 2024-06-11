package service;

import dataaccess.*;

import java.sql.SQLException;

public class ClearService {
    private final UserDAO users;
    private final GameDAO games;
    private final AuthDAO auths;

    public ClearService() {
        // SQL DAOs by default
        this.users = new SQLUserDAO();
        this.games = new SQLGameDAO();
        this.auths = new SQLAuthDAO();
    }

    public ClearService(UserDAO users, GameDAO games, AuthDAO auths) {
         // memory DAOs if they are given
            this.users = users;
            this.games = games;
            this.auths = auths;
    }

    public Object clear() throws DataAccessException {
        try {
            users.removeAllUsers();
            games.removeAllGames();
            auths.removeAllAuthTokens();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        if (clearStatus()) return null;
        else throw new DataAccessException("data not cleared.");
    }

    private boolean clearStatus() throws DataAccessException {
        try {
            return users.isEmpty() && games.isEmpty() && auths.isEmpty(); // returns true if all are empty
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
