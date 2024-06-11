package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;


public class UserService {
    private final UserDAO userDAO = new SQLUserDAO();
    private final AuthDAO authDAO = new SQLAuthDAO();

    public AuthData register(UserData user) throws DataAccessException, SQLException {
        // test if all fields are filled
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("bad request");
        }

        // test if username is already taken
        if (userDAO.getUser(user.username()) != null) {
            throw new DataAccessException("already taken");
        }

        // create user and store in database
        try {
            userDAO.createUser(user);
        } catch (DataAccessException e) {
            throw new DataAccessException("Database error");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String username = user.username();
        String token = authDAO.createAuth(username);

        // registration successful, returning AuthData object
        return new AuthData(token, username);
    }

    public AuthData login(UserData loginData) throws SQLException, DataAccessException {
        // test if all fields are filled
        if (loginData.username() == null || loginData.password() == null) {
            throw new DataAccessException("must fill all fields");
        }

        // check for bad username
        if (userDAO.getUser(loginData.username()) == null) {
            throw new DataAccessException("unauthorized");
        }

        // check if given password matches the one in the database
        UserData savedUser = userDAO.getUser(loginData.username());
        if (!verifyPassword(loginData.password(), savedUser.password())) {
            throw new DataAccessException("unauthorized");
        }

        // correct login info given, returning AuthData object
        return new AuthData(authDAO.createAuth(loginData.username()), loginData.username());
    }

    public boolean logout(String authToken) throws SQLException, DataAccessException {
        // check if authToken is correct
        if (verifyAuth(authToken)) {
            // delete authToken from database
            authDAO.deleteAuth(authToken);
        }

        // return true if logout is successful
        return authDAO.getAuth(authToken) == null;
    }

    private boolean verifyPassword(String givenPassword, String savedHashedPassword) {
        return BCrypt.checkpw(givenPassword, savedHashedPassword);
    }

    private boolean verifyAuth(String authToken) throws SQLException, DataAccessException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("unauthorized");
        } else return true;
    }
}
