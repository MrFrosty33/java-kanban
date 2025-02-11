package api.handlers;

import api.Endpoint;
import api.adapters.TaskListTypeToken;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import exceptions.ValidateTimeException;
import interfaces.TaskManager;
import models.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        // чтобы избавиться от "" на 0 индексе
        path = Arrays.copyOfRange(path, 1, path.length);

        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());

        switch (endpoint) {
            case GET:
                try {
                    if (path.length == 2) {
                        Task task = manager.getTask(Integer.parseInt(path[1]));
                        String taskJson = gson.toJson(task);
                        sendResponse(exchange, taskJson, 200);
                    } else if (path.length == 1) {
                        ArrayList<Task> tasks = manager.getAllTasks();
                        String tasksJson = gson.toJson(tasks, new TaskListTypeToken().getType());
                        sendResponse(exchange, tasksJson, 200);
                    } else {
                        sendWrongPath(exchange);
                    }
                } catch (NotFoundException ex) {
                    sendNotFound(exchange);
                } catch (NumberFormatException e) {
                    sendWrongPath(exchange);
                } catch (Exception e) {
                    sendInternalServerError(exchange);
                }
                break;
            case POST:
                try {
                    String json = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(json, Task.class);

                    if (path.length == 2) {
                        manager.updateTask(task);
                        sendResponse(exchange, null, 201);
                    } else if (path.length == 1) {
                        manager.addTask(task);
                        sendResponse(exchange, null, 201);
                    } else {
                        sendWrongPath(exchange);
                    }
                } catch (ValidateTimeException ex) {
                    sendHasInteractions(exchange);
                } catch (JsonSyntaxException ex) {
                    sendJsonSyntaxError(exchange);
                } catch (NumberFormatException e) {
                    sendWrongPath(exchange);
                } catch (Exception ex) {
                    sendInternalServerError(exchange);
                }
                break;
            case DELETE:
                try {
                    if (path.length == 2) {
                        manager.removeTask(Integer.parseInt(path[1]));
                        sendResponse(exchange, null, 200);
                    } else if (path.length == 1) {
                        manager.removeAllTasks();
                        sendResponse(exchange, null, 200);
                    } else {
                        sendWrongPath(exchange);
                    }
                } catch (NotFoundException e) {
                    sendNotFound(exchange);
                } catch (NumberFormatException e) {
                    sendWrongPath(exchange);
                } catch (Exception ex) {
                    sendInternalServerError(exchange);
                }
                break;
            default:
                sendWrongRequestMethod(exchange);
                break;
        }
    }
}
