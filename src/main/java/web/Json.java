package web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

final class Json {

    static final ObjectMapper MAPPER = new ObjectMapper();

    private Json() {
    }

    public static Map<String, Object> readMap(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody()) {
            return MAPPER.readValue(in, new TypeReference<Map<String, Object>>() {});
        }
    }

    static void send(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        if (bytes.length > 0) {
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        }
    }

    static void sendObject(HttpExchange exchange, int status, Object value) throws IOException {
        send(exchange, status, MAPPER.writeValueAsString(value));
    }
}
