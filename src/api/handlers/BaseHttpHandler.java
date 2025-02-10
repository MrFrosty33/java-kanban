package api.handlers;

import api.Endpoint;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected Endpoint getEndpoint(String requestMethod) {
        switch (requestMethod) {
            case "GET":
                return Endpoint.GET;
            case "POST":
                return Endpoint.POST;
            case "DELETE":
                return Endpoint.DELETE;
            default:
                return null;
        }
    }

    protected void sendResponse(HttpExchange exchange, String json, int code) throws IOException {
        if (code == 200) {
            if (json != null && !json.isEmpty()) {
                byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(code, resp.length);
                exchange.getResponseBody().write(resp);
                exchange.close();
            } else {
                byte[] resp = "Json файл был пуст или null".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(code, resp.length);
                exchange.getResponseBody().write(resp);
                exchange.close();
            }
        } else if (code == 201) {
            byte[] resp = "Объект был успешно создан".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(code, resp.length);
            exchange.getResponseBody().write(resp);
            exchange.close();
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        byte[] resp = "Объект не найден.".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(404, 0);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendWrongRequestMethod(HttpExchange exchange) throws IOException {
        byte[] resp = "Недопустимый метод запроса.".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.getResponseHeaders().add("Allow", "GET, POST, DELETE");
        exchange.sendResponseHeaders(405, 0);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendWrongPath(HttpExchange exchange) throws IOException {
        byte[] resp = "Недопустимый путь запроса.".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.getResponseHeaders().add("Allow", "GET, POST, DELETE");
        exchange.sendResponseHeaders(415, 0);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        byte[] resp = "Объект пересекается с другим по времени.".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(406, 0);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendInternalServerError(HttpExchange exchange) throws IOException {
        byte[] resp = "Внутренняя ошибка сервера.".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(500, 0);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendJsonSyntaxError(HttpExchange exchange) throws IOException {
        byte[] resp = "Json файл не распознаётся. \nJson не должен содержать полей exampleField: null ".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(500, 0);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }


}

