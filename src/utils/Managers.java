package utils;

import controllers.InMemoryTaskManager;
import interfaces.HistoryManager;
import controllers.InMemoryHistoryManager;
import interfaces.TaskManager;

public class Managers{

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
