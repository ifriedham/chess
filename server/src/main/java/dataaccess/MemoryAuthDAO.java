package dataaccess;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, String> auths;
    public MemoryAuthDAO() {
        auths = new HashMap<>();
    }

    public String createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        auths.put(authToken, username);
        return authToken;
    }

    public String getAuth(String authToken) {
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    public void removeAllAuthTokens() {
        auths.clear();
    }

    public boolean isEmpty() {
        return auths.isEmpty();
    }
}
