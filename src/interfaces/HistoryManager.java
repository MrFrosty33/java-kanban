package interfaces;

import models.Task;

public interface HistoryManager {

    void addInHistory(Task task);

    Task[] getHistory();
}
