package service;

import dataaccess.*;

public class ClearService {
    private final UserDAO users = new SQLUserDAO();
    private final GameDAO games = new SQLGameDAO();
    private final AuthDAO auths = new SQLAuthDAO();


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
