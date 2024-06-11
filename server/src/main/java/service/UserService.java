package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;


public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() {
        // SQL DAOs by default
        this.userDAO = new SQLUserDAO();
        this.authDAO = new SQLAuthDAO();
    }

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        // memory DAOs if they are given
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

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
            String username = user.username();
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt()); // hash password before storing
            String email = user.email();

            UserData newUser = new UserData(username, hashedPassword, email);

            userDAO.createUser(newUser); // store user in database

            String token = authDAO.createAuth(username); // get auth token for user

            // registration successful, returning AuthData object
            return new AuthData(token, username);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public AuthData login(UserData loginData) throws DataAccessException {
        // test if all fields are filled
        if (loginData.username() == null || loginData.password() == null) {
            throw new DataAccessException("must fill all fields");
        }

        try {
            // check for bad username
            if (userDAO.getUser(loginData.username()) == null) {
                throw new DataAccessException("unauthorized");
            }

            // check if given password matches the one in the database
            String username = loginData.username();

            UserData savedUser = userDAO.getUser(username);
            String givenPassword = loginData.password();
            String savedPassword = savedUser.password(); // should return hashed password
            if (!verifyPassword(givenPassword, savedPassword)) {
                throw new DataAccessException("unauthorized");
            }

            // correct password given, create and return auth token
            String token = authDAO.createAuth(username);
            return new AuthData(token, username);
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
