package api.handlers;

import api.Endpoint;
import api.adapters.DurationAdapter;
import api.adapters.LocalDateTimeAdapter;
import api.adapters.StatusAdapter;
import api.adapters.TaskListTypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;
import models.Status;
import models.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class PrioritizedTasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager;

    public PrioritizedTasksHandler(TaskManager manager) {
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
                ArrayList<Task> tasks = manager.getPrioritizedTasks();
                String tasksJson = gson.toJson(tasks, new TaskListTypeToken().getType());
                if (tasks.isEmpty()) sendNotFound(exchange);
                else sendResponse(exchange, tasksJson, 200);
                break;
            default:
                sendWrongRequestMethod(exchange);
                break;
        }
    }
}
