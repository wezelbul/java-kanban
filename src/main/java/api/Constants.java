package api;

import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Constants {

    public static final int KV_SERVER_PORT = 8078;
    public static final int TASK_SERVER_PORT = 8080;
    public static final int DEFAULT_BACKLOG = 0;
    public static final int DEFAULT_RESPONSE_LENGTH = 0;
    public static final int DEFAULT_DELAY = 1;

    public static final String NEW_LINE = "\n";
    public static final String HTTP_PROTOCOL = "http://";
    public static final String HOSTNAME = "localhost";
    public static final String REQUEST_FORMAT = "application/json";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String API_TOKEN = "API_TOKEN";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT = "Accept";

    public static final Charset STANDARD_CHARSET = StandardCharsets.UTF_8;
    public static final HttpClient.Version CLIENT_VERSION = HttpClient.Version.HTTP_1_1;

}
