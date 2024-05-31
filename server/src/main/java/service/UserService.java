package service;


import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.Objects;

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

        // create user and store in database
        userDAO.createUser(user);
        String username = user.username();
        String token = authDAO.createAuth(username);

        // registration successful, returning AuthData object
        return new AuthData(token, username);
    }

    public AuthData login(UserData loginData) throws DataAccessException {
        // test if all fields are filled
        if (loginData.username() == null || loginData.password() == null) {
            throw new DataAccessException("must fill all fields");
        }

        // check for bad username
        if (userDAO.getUser(loginData.username()) == null) {
            throw new DataAccessException("no account with that username found");
        }

        // check if given password matches the one in the database
        UserData savedUser = userDAO.getUser(loginData.username());
        if (!verifyPassword(loginData.password(), savedUser.password())) {
            throw new DataAccessException("unauthorized");
        }

        // correct login info given, returning AuthData object
        return new AuthData(authDAO.createAuth(loginData.username()), loginData.username());
    }

    public boolean logout(AuthData userAuth) throws DataAccessException {
        // check if authToken is correct
        if (verifyAuth(userAuth)) {
            // delete authToken from database
            authDAO.deleteAuth(userAuth.authToken());
        }

        // return true if logout is successful
        return authDAO.getAuth(userAuth.authToken()) == null;
    }

    private boolean verifyPassword(String givenPassword, String savedPassword) {
        /* TODO: FIGURE OUT HOW TO COMPARE THE PASSWORD TO A HASH */

        return givenPassword.equals(savedPassword);
    }

    private boolean verifyAuth(AuthData userAuth) throws DataAccessException {
        if (userAuth == null || authDAO.getAuth(userAuth.authToken()) == null) {
            throw new DataAccessException("unauthorized");
        } else return true;
    }
}
