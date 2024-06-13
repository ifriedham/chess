package client;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import com.google.gson.Gson;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerFacade {
    int port;
    String baseUrl;

    ServerFacade(int givenPort) {
        port = givenPort;
        baseUrl = "http://localhost:" + port;
    }

    public Object doPost(URL url, JsonObject reqJson) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        //write out header
        connection.addRequestProperty("Content-Type", "application/json");
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
            return null;
        }
    }

    public Object register(String username, String password, String email) throws IOException {
        URL url = new URL(baseUrl + "/user");

        // Create JSON object with registration details
        JsonObject reqJson = new JsonObject();
        reqJson.addProperty("username", username);
        reqJson.addProperty("password", password);
        reqJson.addProperty("email", email);
        return doPost(url, reqJson);
    }

    public void clear() {
    }
}
