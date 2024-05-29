package service;


import dataaccess.*;
import model.AuthData;
import model.UserData;

public class UserService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }


    public AuthData register(UserData user) throws DataAccessException {
         // test if all fields are filled
         if (user.username() == null || user.password() == null || user.email() == null) {
             throw new DataAccessException("bad request");
         }

        // test if username is already taken
        if (userDAO.getUser(user.username()) != null) {
            throw new DataAccessException("already taken");
        }

        userDAO.createUser(user);
        String username = user.username();
        String token = authDAO.createAuth(username);
        return new AuthData(username, token);
    }

    public AuthData login(UserData user) {
        return null;
    }

    public void logout(UserData user) {
    }

}
