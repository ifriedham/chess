package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> users;
    public MemoryUserDAO() {
        users = new HashMap<>();
    }

    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    public UserData getUser(String username) {
        if (users != null) return users.get(username);
        else return null;
    }

    public void removeAllUsers() {
        users.clear();
    }
}
