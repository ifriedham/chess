package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;


public class UserService {
    private final UserDAO userDAO = new SQLUserDAO();
    private final AuthDAO authDAO = new SQLAuthDAO();

    public AuthData register(UserData user) throws DataAccessException {

        // test if all fields are filled
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("bad request");
        }

        // test if username is already taken
        try {
            if (userDAO.getUser(user.username()) != null) {
                throw new DataAccessException("already taken");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        // create user and store in database
        try {
            userDAO.createUser(user);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Database error");
        }

        String username = user.username();
        String token = null;
        try {
            token = authDAO.createAuth(username);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        // registration successful, returning AuthData object
        return new AuthData(token, username);
    }

    public AuthData login(UserData loginData) throws DataAccessException {
        // test if all fields are filled
        if (loginData.username() == null || loginData.password() == null) {
            throw new DataAccessException("must fill all fields");
        }

        // check for bad username
        try {
            if (userDAO.getUser(loginData.username()) == null) {
                throw new DataAccessException("unauthorized");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        // check if given password matches the one in the database
        UserData savedUser = null;
        try {
            savedUser = userDAO.getUser(loginData.username());
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        if (!verifyPassword(loginData.password(), savedUser.password())) {
            throw new DataAccessException("unauthorized");
        }

        // correct login info given, returning AuthData object
        try {
            return new AuthData(authDAO.createAuth(loginData.username()), loginData.username());
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public boolean logout(String authToken) throws DataAccessException {
        // check if authToken is correct
        if (verifyAuth(authToken)) {
            // delete authToken from database
            try {
                authDAO.deleteAuth(authToken);
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }

        // return true if logout is successful
        try {
            return authDAO.getAuth(authToken) == null;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private boolean verifyPassword(String givenPassword, String savedHashedPassword) {
        return BCrypt.checkpw(givenPassword, savedHashedPassword);
    }

    private boolean verifyAuth(String authToken) throws DataAccessException {
        try {
            if (authToken == null || authDAO.getAuth(authToken) == null) {
                throw new DataAccessException("unauthorized");
            } else return true;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
