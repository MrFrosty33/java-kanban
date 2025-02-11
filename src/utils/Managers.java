package utils;

import controllers.InMemoryHistoryManager;
import controllers.InMemoryTaskManager;
import interfaces.HistoryManager;
import interfaces.TaskManager;

public abstract class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
