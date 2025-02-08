package API.handlers;

import API.Endpoint;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler {

    protected Endpoint getEndpoint(String requestMethod) {
        switch (requestMethod){
            case "GET":
                return Endpoint.GET;
            case "POST":
                return Endpoint.POST;
            case "DELETE":
                return Endpoint.DELETE;
            default:
                //TODO что делать, если иной запрос?
                return null;
        }
    }

    protected void writeResponse(HttpExchange exchange, String json, int code) throws IOException {
        if(code == 200) {
            byte[] resp = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(code, resp.length);
            exchange.getResponseBody().write(resp);
            exchange.close();
        } else {
            exchange.sendResponseHeaders(code, 0);
        }
    }


}

