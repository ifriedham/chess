package client;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import com.google.gson.Gson;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.reflect.TypeToken;
import model.*;

public class ServerFacade {
    int port;
    String baseUrl;
    String authToken;

    public ServerFacade(int givenPort) {
        port = givenPort;
        baseUrl = "http://localhost:" + port;
        authToken = null;
    }

    public Map<String, String> doPost(URL url, JsonObject reqJson) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        //write out header
        connection.addRequestProperty("authorization", authToken);

        // write json to output stream
        try (OutputStream outputStream = connection.getOutputStream()) {
            var jsonBody = new Gson().toJson(reqJson);
            outputStream.write(jsonBody.getBytes());
        }

        // get response
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream resBody = connection.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(resBody);
                return new Gson().fromJson(reader, Map.class);
            }
        }
        else {
            InputStream responseBody = connection.getErrorStream();
            throw new IOException("Failed to post: HTTP error code : " + responseBody);
        }
    }

    public Map<String, String> doGet (URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        connection.addRequestProperty("Authorization", authToken);

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream resBody = connection.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(resBody);
                return new Gson().fromJson(reader, Map.class);
            }
        } else {
            InputStream responseBody = connection.getErrorStream();
            throw new IOException("Failed to get: HTTP error code : " + responseBody);
        }
    }

    public int doPut (URL url, JsonObject reqJson) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);

        //write out header
        connection.addRequestProperty("Authorization", authToken);

        // write json to output stream
        try (OutputStream outputStream = connection.getOutputStream()) {
            var jsonBody = new Gson().toJson(reqJson);
            outputStream.write(jsonBody.getBytes());
        }

        // check response
        int responseCode = connection.getResponseCode();
        //System.out.println("Response Code: " + responseCode);
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to join game: HTTP error code : " + responseCode);
        }

        return responseCode;
    }

    public int doDelete(URL url, JsonObject reqJson) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("DELETE");
        connection.setDoOutput(true);

        //write out header
        connection.addRequestProperty("Authorization", authToken);

        // write json to output stream
        try (OutputStream outputStream = connection.getOutputStream()) {
            var jsonBody = new Gson().toJson(reqJson);
            outputStream.write(jsonBody.getBytes());
        }

        // check response
        int responseCode = connection.getResponseCode();
        //System.out.println("Response Code: " + responseCode);
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to delete: HTTP error code : " + responseCode);
        }

        return responseCode;
    }


    public AuthData register(String username, String password, String email) throws IOException {
        URL url = new URL(baseUrl + "/user");

        // Create JSON object with registration details
        JsonObject reqJson = new JsonObject();
        reqJson.addProperty("username", username);
        reqJson.addProperty("password", password);
        reqJson.addProperty("email", email);
        Map<String, String> res = doPost(url, reqJson);


        return new AuthData(res.get("authToken"), res.get("username"));
    }

    public AuthData login(String username, String password) throws IOException {
        URL url = new URL(baseUrl + "/session");

        // Create JSON object with login details
        JsonObject reqJson = new JsonObject();
        reqJson.addProperty("username", username);
        reqJson.addProperty("password", password);
        Map<String, String> res = doPost(url, reqJson);

        return new AuthData(res.get("authToken"), res.get("username"));
    }

    public int logout(String authToken) throws IOException {
        this.authToken = authToken;
        URL url = new URL(baseUrl + "/session");
        JsonObject reqJson = new JsonObject();
        reqJson.addProperty("authToken", authToken);
        return doDelete(url, reqJson);
    }

    public Collection<GameData> listGames(String authToken) throws IOException {
        this.authToken = authToken;
        URL url = new URL(baseUrl + "/game");

        // Create JSON object with auth token
        JsonObject reqJson = new JsonObject();
        reqJson.addProperty("authToken", authToken);

        // get list
        Map<String, String> res = doGet(url);

        // convert list to collection
        Type collectionType = new TypeToken<Collection<GameData>>() {}.getType();
        Collection<GameData> gameList = new Gson().fromJson(new Gson().toJson(res.get("games")), collectionType);

        return gameList;
    }


        public Integer createGame(String authToken, String gameName) throws IOException {
        this.authToken = authToken;
        URL url = new URL(baseUrl + "/game");

        // Create JSON object with game name and auth token
        JsonObject reqJson = new JsonObject();
        reqJson.addProperty("authToken", authToken);
        reqJson.addProperty("gameName", gameName);
        Map<String, String> res = doPost(url, reqJson);

        return Integer.valueOf(res.get("gameID"));
    }

    public int joinGame(String authToken, String playerColor, int gameId) throws IOException {
        this.authToken = authToken;
        URL url = new URL(baseUrl + "/game");

        // Create JSON object with auth token, player color, and game ID
        JsonObject reqJson = new JsonObject();
        reqJson.addProperty("authToken", authToken);
        reqJson.addProperty("playerColor", playerColor);
        reqJson.addProperty("gameID", gameId);

        return doPut(url, reqJson);
    }

    public void clear() throws IOException {
        URL url = new URL(baseUrl + "/db");
        JsonObject reqJson = new JsonObject();
        doDelete(url, reqJson);
    }
}
