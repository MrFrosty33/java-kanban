package API.handlers;

import API.Endpoint;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import exceptions.ValidateTimeException;
import interfaces.TaskManager;
import models.Task;
import utils.Managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("TaskHandler получил запрос");

        String[] rawPath = exchange.getRequestURI().getPath().split("/");
        String[] path = Arrays.copyOfRange(rawPath, 1, rawPath.length);
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(exchange.getRequestBody().toString());
        JsonObject jsonObject = new JsonObject();
        if(jsonElement.isJsonObject()) gson.toJson(jsonElement);
        //TODO проверить, работает ли вообще
        switch (endpoint) {
            case GET:
                try {
                    if (path.length == 2) {
                        Task task = manager.getTask(Integer.parseInt(path[1]));
                        //TODO зависает при выполнении этой строке!?
                        // что так, что этак зависает...
                        String taskJson = JsonParser.parseString(task.toString()).getAsString();
                        //String taskJson = gson.toJson(task);
                        sendResponse(exchange, taskJson, 200);
                    } else {
                        ArrayList<Task> tasks = manager.getAllTasks();
                        String tasksJson = gson.toJson(tasks);
                        sendResponse(exchange, tasksJson, 200);
                    }
                } catch (NotFoundException ex){
                    sendNotFound(exchange);
                }
                break;
            case POST:
                //TODO проверка, является ли Task правильным объектом?
                try {
                    Task task = gson.fromJson(jsonObject, Task.class);
                    if (path.length == 2) {
                        manager.updateTask(task);
                        sendResponse(exchange, null, 201);
                    } else {
                        manager.addTask(task);
                        sendResponse(exchange, null, 201);
                    }
                } catch (ValidateTimeException ex){
                    sendHasInteractions(exchange);
                }
                break;
            case DELETE:
                if(path.length == 2){
                    // нужно возвращать тот объект, что был удалён?
                    Task taskToRemove = manager.getTask(Integer.parseInt(path[1]));
                    manager.removeTask(Integer.parseInt(path[1]));
                    String taskJson = gson.toJson(taskToRemove);
                    sendResponse(exchange, taskJson, 200);
                } else {
                    manager.removeAllTasks();
                    sendResponse(exchange, null, 201);
                }
                break;
            default:
                sendWrongRequestMethod(exchange);
                break;
        }
    }
}
