package api.servers;

import api.Constants;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.net.HttpURLConnection.*;
import static api.Constants.*;

public class KVServer {

    private static final String SAVE = "/save";
    private static final String LOAD = "/load";
    private static final String REGISTER = "/register";

    private final String KVSERVER_URL = HTTP_PROTOCOL + HOSTNAME + ":" + KV_SERVER_PORT + "/";
    private final String API_TOKEN;
    private HttpServer server;
    private Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {

        API_TOKEN = genApiKey();
        server = HttpServer.create(new InetSocketAddress(HOSTNAME, KV_SERVER_PORT), DEFAULT_BACKLOG);
        server.createContext(REGISTER, this::register);
        server.createContext(SAVE, this::save);
        server.createContext(LOAD, this::load);
    }

    public String getUrl() {
        return KVSERVER_URL;
    }

    private void register(HttpExchange exchange) throws IOException {
        try {
            System.out.println(NEW_LINE + REGISTER);
            if (GET.equals(exchange.getRequestMethod())) {
                sendText(exchange, API_TOKEN);
            } else {
                System.out.println(REGISTER + " ждёт " + GET + "-запрос, а получил " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(HTTP_BAD_METHOD, DEFAULT_RESPONSE_LENGTH);
            }
        } finally {
            exchange.close();
        }
    }

    private void save(HttpExchange exchange) throws IOException {
        try {
            System.out.println(NEW_LINE + SAVE);
            if (!hasAuth(exchange)) {
                System.out.println("Запрос неавторизован, нужен параметр " + Constants.API_TOKEN);
                exchange.sendResponseHeaders(HTTP_FORBIDDEN, DEFAULT_RESPONSE_LENGTH);
                return;
            }
            if (POST.equals(exchange.getRequestMethod())) {
                String key = exchange.getRequestURI().getPath().substring((SAVE + "/").length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пуст. Значение key указывается по пути: " + SAVE + "/{key}");
                    exchange.sendResponseHeaders(HTTP_BAD_REQUEST, DEFAULT_RESPONSE_LENGTH);
                    return;
                }
                String value = readText(exchange);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пуст. Значение value указывается в теле запроса");
                    exchange.sendResponseHeaders(HTTP_BAD_REQUEST, DEFAULT_RESPONSE_LENGTH);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                exchange.sendResponseHeaders(HTTP_OK, DEFAULT_RESPONSE_LENGTH);
            } else {
                System.out.println(SAVE + " ждёт " + POST + "-запрос, а получил: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(HTTP_BAD_METHOD, DEFAULT_RESPONSE_LENGTH);
            }
        } finally {
            exchange.close();
        }
    }

    private void load(HttpExchange exchange) throws IOException {
        try {
            System.out.println(NEW_LINE + LOAD);
            if (!hasAuth(exchange)) {
                System.out.println("Запрос неавторизован, нужен параметр " + Constants.API_TOKEN);
                exchange.sendResponseHeaders(HTTP_FORBIDDEN, DEFAULT_RESPONSE_LENGTH);
                return;
            }
            if (GET.equals(exchange.getRequestMethod())) {
                String key = exchange.getRequestURI().getPath().substring((LOAD +"/").length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /" + SAVE + "{key}");
                    exchange.sendResponseHeaders(HTTP_BAD_REQUEST, DEFAULT_RESPONSE_LENGTH);
                    return;
                }
                if (!data.containsKey(key)) {
                    System.out.println("Не могу достать данные для ключа '" + key + "', данные отсутствуют");
                    exchange.sendResponseHeaders(HTTP_NOT_FOUND, DEFAULT_RESPONSE_LENGTH);
                    return;
                }
                sendText(exchange, data.get(key));
                System.out.println("Значение для ключа " + key + " успешно получено!");
                exchange.sendResponseHeaders(HTTP_OK, DEFAULT_RESPONSE_LENGTH);
            } else {
                System.out.println(LOAD + " ждёт " + GET + "-запрос, а получил: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(HTTP_BAD_METHOD, DEFAULT_RESPONSE_LENGTH);
            }
        } finally {
            exchange.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + KV_SERVER_PORT);
        System.out.println("Открой в браузере " + KVSERVER_URL);
        System.out.println(Constants.API_TOKEN + ": " + API_TOKEN);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер на порту " + KV_SERVER_PORT + " остановлен");
    }

    private String genApiKey() {
        return UUID.randomUUID().toString();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains(Constants.API_TOKEN + "=" + API_TOKEN) || rawQuery.contains(Constants.API_TOKEN + "=DEBUG"));
    }

    protected String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), STANDARD_CHARSET);
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(STANDARD_CHARSET);
        exchange.getResponseHeaders().add(HEADER_CONTENT_TYPE, REQUEST_FORMAT);
        exchange.sendResponseHeaders(HTTP_OK, resp.length);
        exchange.getResponseBody().write(resp);
    }
}
