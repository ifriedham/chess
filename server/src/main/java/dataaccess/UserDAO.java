package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void removeAllUsers() throws DataAccessException;

    boolean isEmpty() throws DataAccessException;
}
