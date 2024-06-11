package dataaccess;

import java.sql.SQLException;

public interface AuthDAO {
    String createAuth(String username) throws DataAccessException, SQLException;

    String getAuth(String authToken) throws DataAccessException, SQLException;

    String getUsername(String authToken) throws DataAccessException, SQLException;

    void deleteAuth(String authToken) throws DataAccessException, SQLException;

    void removeAllAuthTokens() throws DataAccessException, SQLException;

    boolean isEmpty() throws DataAccessException, SQLException;
}
