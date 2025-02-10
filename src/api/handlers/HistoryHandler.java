package api.handlers;

import api.Endpoint;
import api.adapters.DurationAdapter;
import api.adapters.LocalDateTimeAdapter;
import api.adapters.StatusAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.InMemoryTaskManager;
import interfaces.HistoryManager;
import models.Status;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private InMemoryTaskManager manager;

    public HistoryHandler(InMemoryTaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // чтобы избавиться от "" на 0 индексе
        String[] path = exchange.getRequestURI().getPath().split("/");
        path = Arrays.copyOfRange(path, 1, path.length);

        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();

        switch (endpoint) {
            case GET:
                HistoryManager history = manager.getHistory();
                String historyJson = gson.toJson(history.getHistory());
                sendResponse(exchange, historyJson, 200);
            default:
                sendWrongRequestMethod(exchange);
                break;
        }
    }
}
