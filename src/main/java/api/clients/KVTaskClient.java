package api.clients;

import api.Constants;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

import static api.Constants.*;

public class KVTaskClient {

    private static final String SAVE = "save/";
    private static final String LOAD = "load/";
    private static final String REGISTER = "register/";
    private static final char REQ_CHAR = '?';
    private static final String API_TOKEN = REQ_CHAR + Constants.API_TOKEN + '=';

    private String apiTokenReq;
    private String url;
    HttpClient client;

    public KVTaskClient(String url) {
        this.url = url;
        this.client = HttpClient.newHttpClient();
        this.apiTokenReq = API_TOKEN + getApiToken();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по адресу: '" + request.uri().toString()
                    + "' возникла ошибка.");
            e.printStackTrace();
            return null;
        }
    }

    private String getApiToken() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + REGISTER))
                .GET()
                .version(CLIENT_VERSION)
                .build();

        return sendRequest(request).body();
    }

    public void put(String key, String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + SAVE + key + apiTokenReq))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header(HEADER_ACCEPT, REQUEST_FORMAT)
                .version(CLIENT_VERSION)
                .build();

        sendRequest(request);
    }

    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + LOAD + key + apiTokenReq))
                .GET()
                .version(CLIENT_VERSION)
                .build();

        return sendRequest(request).body();
    }

}
