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
    /* TODO: STORE PASSWORD AS A HASH */

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void removeAllUsers() {
        users.clear();
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }
}
