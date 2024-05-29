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
        return new AuthData(token, username);
    }

    public AuthData login(UserData loginData) throws DataAccessException {
        UserData savedUser = userDAO.getUser(loginData.username());

        if (!verifyLogin(loginData.password(), savedUser.password())) {
            throw new DataAccessException("unauthorized");
        }

        return new AuthData(authDAO.createAuth(loginData.username()), loginData.username());
    }

    private boolean verifyLogin(String givenPassword, String savedPassword) {
        return givenPassword.equals(savedPassword);
    }

    public void logout(UserData user) throws DataAccessException {
    }

}
