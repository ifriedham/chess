package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException, SQLException;

    UserData getUser(String username) throws DataAccessException, SQLException;

    void removeAllUsers() throws DataAccessException, SQLException;

    boolean isEmpty() throws DataAccessException, SQLException;
}
