package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO{

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void removeAllUsers() throws DataAccessException {

    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        return false;
    }
}
