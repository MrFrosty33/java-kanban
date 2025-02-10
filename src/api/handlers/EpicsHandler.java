package api.handlers;

import api.Endpoint;
import api.adapters.*;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.InMemoryTaskManager;
import exceptions.NotFoundException;
import exceptions.ValidateTimeException;
import models.Epic;
import models.Status;
import models.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private InMemoryTaskManager manager;

    public EpicsHandler(InMemoryTaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        // чтобы избавиться от "" на 0 индексе
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
                try {
                    if (path.length == 3) {
                        if (path[2].equals("subtasks")) {
                            Epic epic = manager.getEpic(Integer.parseInt(path[1]));
                            ArrayList<Subtask> subtasksFromEpic = manager.getSubtasksFromEpic(epic);
                            String subtasksFromEpicJson =
                                    gson.toJson(subtasksFromEpic, new SubtaskListTypeToken().getType());
                            sendResponse(exchange, subtasksFromEpicJson, 200);

                        } else {
                            sendWrongPath(exchange);
                        }
                    } else if (path.length == 2) {
                        Epic epic = manager.getEpic(Integer.parseInt(path[1]));
                        String epicJson = gson.toJson(epic);
                        sendResponse(exchange, epicJson, 200);
                    } else if (path.length == 1) {
                        ArrayList<Epic> epics = manager.getAllEpics();
                        String epicsJson = gson.toJson(epics, new EpicListTypeToken().getType());
                        sendResponse(exchange, epicsJson, 200);
                    } else {
                        sendWrongPath(exchange);
                    }
                } catch (NotFoundException ex) {
                    sendNotFound(exchange);
                } catch (NumberFormatException e) {
                    sendWrongPath(exchange);
                } catch (Throwable e) {
                    sendInternalServerError(exchange);
                }
                break;
            case POST:
                try {
                    String json = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(json, Epic.class);

                    if (path.length == 1) {
                        manager.addEpic(epic);
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
                } catch (Throwable ex) {
                    sendInternalServerError(exchange);
                }
                break;
            case DELETE:
                try {
                    if (path.length == 2) {
                        // нужно возвращать тот объект, что был удалён?
                        //Epic epicToRemove = manager.getEpic(Integer.parseInt(path[1]));
                        manager.removeEpic(Integer.parseInt(path[1]));
                        //String epicToRemove = gson.toJson(epicToRemove);
                        sendResponse(exchange, null, 200);
                    } else if (path.length == 1) {
                        manager.removeAllEpics();
                        sendResponse(exchange, null, 200);
                    } else {
                        sendWrongPath(exchange);
                    }
                } catch (NotFoundException e) {
                    sendNotFound(exchange);
                } catch (NumberFormatException e) {
                    sendWrongPath(exchange);
                } catch (Throwable ex) {
                    sendInternalServerError(exchange);
                }
                break;
            default:
                sendWrongRequestMethod(exchange);
                break;
        }
    }
}
