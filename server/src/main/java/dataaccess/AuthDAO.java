package dataaccess;

public interface AuthDAO {
    String createAuth(String username) throws DataAccessException;

    String getAuth(String authToken) throws DataAccessException;

    String getUsername(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void removeAllAuthTokens() throws DataAccessException;

    boolean isEmpty() throws DataAccessException;
}
