package API.handlers;

import API.Endpoint;
import API.adapters.DurationAdapter;
import API.adapters.LocalDateTimeAdapter;
import API.adapters.StatusAdapter;
import API.adapters.TaskListTypeToken;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.InMemoryTaskManager;
import exceptions.NotFoundException;
import exceptions.ValidateTimeException;
import models.Status;
import models.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private InMemoryTaskManager manager;

    public TasksHandler(InMemoryTaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("TaskHandler получил запрос");

        // чтобы избавиться от "" на 0 индексе
        String[] path = exchange.getRequestURI().getPath().split("/");
        path = Arrays.copyOfRange(path, 1, path.length);

        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        switch (endpoint) {
            case GET:
                try {
                    if (path.length == 2) {
                        Task task = manager.getTask(Integer.parseInt(path[1]));
                        String taskJson = gson.toJson(task);
                        sendResponse(exchange, taskJson, 200);
                    } else {
                        ArrayList<Task> tasks = manager.getAllTasks();
                        String tasksJson = gson.toJson(tasks, new TaskListTypeToken().getType());
                        sendResponse(exchange, tasksJson, 200);
                    }
                } catch (NotFoundException ex) {
                    sendNotFound(exchange);
                } catch (Throwable e) {
                    sendInternalServerError(exchange);
                }
                break;
            case POST:
                try {
                    //TODO не работает.
                    // Сетует на com.google.gson.JsonSyntaxException:
                    // java.lang.IllegalStateException:
                    // Expected a string but was NAME at line 7 column 3 path $.startTime
                    // на данный момент с этим я разобраться не могу
                    String json = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(json, Task.class);
                    System.out.println(task);

                    if (path.length == 2) {
                        manager.updateTask(task);
                        sendResponse(exchange, null, 201);
                    } else {
                        manager.addTask(task);
                        sendResponse(exchange, null, 201);
                    }
                } catch (ValidateTimeException ex) {
                    sendHasInteractions(exchange);
                } catch (Throwable ex) {
                    sendInternalServerError(exchange);
                }
                break;
            case DELETE:
                // Добавил также возможность удалять все задачи, если не указан id удаляемой задачи
                // добавил код ошибки 404, в случае, если указан id задачи, которой нет в списках
                try {
                    if (path.length == 2) {
                        // нужно возвращать тот объект, что был удалён?
                        Task taskToRemove = manager.getTask(Integer.parseInt(path[1]));
                        manager.removeTask(Integer.parseInt(path[1]));
                        //String taskJson = gson.toJson(taskToRemove);
                        sendResponse(exchange, null, 200);
                    } else {
                        manager.removeAllTasks();
                        sendResponse(exchange, null, 200);
                    }
                } catch (NotFoundException e) {
                    sendNotFound(exchange);
                }
                break;
            default:
                sendWrongRequestMethod(exchange);
                break;
        }
    }
}
