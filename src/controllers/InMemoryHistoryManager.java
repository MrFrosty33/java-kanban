package controllers;

import interfaces.HistoryManager;
import models.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private int nextIndexHistory = 0;
    private ArrayList<Task> history = new ArrayList<>(10);

    /**
     * ----- HistoryManager -----
     */

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyCopy = history;
        return historyCopy;
    }

    @Override
    public void addInHistory(Task task) {
        // добавлял отдельную переменную, чтобы в будущем, через неё расширять размер списка истории.
        // Но если советуете, сделать так, то сделаю так.
        if (task == null) {
            return;
        }
        history.add(task);
        if (history.size() > 10) {
            history.removeFirst();
        }
    }

}
