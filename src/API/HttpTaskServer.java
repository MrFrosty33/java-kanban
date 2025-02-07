package API;

import API.handlers.*;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class HttpTaskServer {

    public static void main(String[] args) {
        try {
            // чтобы в будущем можно было тут добавить необходимый путь, при надобности
            HashMap<String, HttpHandler> contexts = new HashMap<>();
            contexts.put("/tasks", new TasksHandler());
            contexts.put("/subtasks", new SubtasksHandler());
            contexts.put("/epics", new EpicsHandler());
            contexts.put("/history", new HistoryHandler());
            contexts.put("/prioritized", new PrioritizedHandler());

            HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
            contexts.entrySet().stream()
                    .forEach(entry -> httpServer.createContext(entry.getKey(), entry.getValue()));



            //TODO обработка исключений
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }


}
