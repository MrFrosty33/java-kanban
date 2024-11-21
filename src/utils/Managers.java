package utils;

import interfaces.HistoryManager;
import controllers.InMemoryHistoryManager;
import interfaces.TaskManager;

public class Managers<T extends TaskManager> {
    private T manager;

    public TaskManager getDefault() {
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
