package api;

import api.handlers.*;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controllers.InMemoryTaskManager;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class HttpTaskServer {
    private HttpServer httpServer;
    private InMemoryTaskManager manager;

    public HttpTaskServer(InMemoryTaskManager manager) {
        this.manager = manager;
    }

    public void start() {
        try {
            // чтобы в будущем можно было тут добавить необходимый путь, при надобности
            HashMap<String, HttpHandler> contexts = new HashMap<>();
            contexts.put("/tasks", new TasksHandler(manager));
            contexts.put("/subtasks", new SubtasksHandler(manager));
            contexts.put("/epics", new EpicsHandler(manager));
            contexts.put("/history", new HistoryHandler(manager));
            contexts.put("/prioritizedTasks", new PrioritizedTasksHandler(manager));
            contexts.put("/prioritizedSubtasks", new PrioritizedSubtasksHandler(manager));

            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
            contexts.entrySet().stream()
                    .forEach(entry -> httpServer.createContext(entry.getKey(), entry.getValue()));

            httpServer.start();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (httpServer != null) httpServer.stop(0);
        System.out.println("Сервер остановлен.");
    }


}
