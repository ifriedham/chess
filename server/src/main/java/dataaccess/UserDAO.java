package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    //void createUser(String username, String password, String email) throws DataAccessException;
    void removeAllUsers() throws DataAccessException;
    boolean isEmpty() throws DataAccessException;
}
