package service;

import dataaccess.*;

public class ClearService {
    private final UserDAO users;
    private final GameDAO games;
    private final AuthDAO auths;

    public ClearService(UserDAO users, GameDAO games, AuthDAO auths) {
        this.users = users;
        this.games = games;
        this.auths = auths;
    }

    public Object clear() throws DataAccessException {
        users.removeAllUsers();
        games.removeAllGames();
        auths.removeAllAuthTokens();

        if (clearStatus()) return null;
        else throw new DataAccessException("data not cleared.");
    }

    private boolean clearStatus() throws DataAccessException {
        return users.isEmpty() && games.isEmpty() && auths.isEmpty();
    }

}
