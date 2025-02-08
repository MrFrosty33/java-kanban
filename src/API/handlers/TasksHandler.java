package API.handlers;

import API.Endpoint;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ValidateTimeException;
import interfaces.TaskManager;
import models.Task;
import utils.Managers;

import java.io.IOException;
import java.util.ArrayList;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager = Managers.getDefault();


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
        Gson gson = new Gson();
        JsonElement jsonElement = JsonParser.parseString(exchange.getRequestBody().toString());
        JsonObject jsonObject = new JsonObject();
        if(jsonElement.isJsonObject()) gson.toJson(jsonElement);
        //TODO сценарии ответа с нужными кодами. Отлавливать исключения и тогда отсылать другие коды?
        //TODO проверить, работает ли вообще
        switch (endpoint) {
            //TODO во всех случаях ответы
            case GET:
                //TODO вернуть taskJson в виде ответа
                if (path.length == 2) {
                    Task task = manager.getTask(Integer.parseInt(path[1]));
                    String taskJson = gson.toJson(task);
                    writeResponse(exchange, taskJson, 200);
                } else {
                    ArrayList<Task> tasks = manager.getAllTasks();
                    String tasksJson = gson.toJson(tasks);
                    writeResponse(exchange, tasksJson, 200);
                }
                break;
            case POST:
                //TODO проверка, является ли Task правильным объектом?
                try {
                    Task task = gson.fromJson(jsonObject, Task.class);
                    if (path.length == 2) {
                        manager.updateTask(task);
                        writeResponse(exchange, null, 201);
                    } else {
                        manager.addTask(task);
                        writeResponse(exchange, null, 201);
                    }
                } catch (ValidateTimeException ex){
                    writeResponse(exchange, null, 406);
                }
                break;
            case DELETE:
                if(path.length == 2){
                    // нужно возвращать тот объект, что был удалён?
                    Task taskToRemove = manager.getTask(Integer.parseInt(path[1]));
                    manager.removeTask(Integer.parseInt(path[1]));
                    String taskJson = gson.toJson(taskToRemove);
                    writeResponse(exchange, taskJson, 200);
                } else {
                    manager.removeAllTasks();
                    writeResponse(exchange, null, 201);
                }
                break;
        }
    }
}
