package service;

import dataaccess.*;

public class ClearService {
    private UserDAO users;
    private GameDAO games;
    private AuthDAO auths;

    public ClearService(UserDAO users, GameDAO games, AuthDAO auths) {
        this.users = users;
        this.games = games;
        this.auths = auths;
    }

    public Object clear() throws DataAccessException{
        users.removeAllUsers();
        games.removeAllGames();
        auths.removeAllAuthTokens();

        if (clearStatus()) return null;
        else return "Error: data not cleared.";
    }

    private boolean clearStatus() throws DataAccessException {
        if (users.isEmpty() && games.isEmpty() && auths.isEmpty()) return true;
        else return false;
    }

}
