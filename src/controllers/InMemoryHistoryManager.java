package controllers;

import interfaces.HistoryManager;
import models.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private int currentHistoryLength = 10;
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
        if (task != null) {
            if (nextIndexHistory++ <= currentHistoryLength - 1) {
                history.add(task);
            } else {
                history.removeFirst();
                history.add(task);
            }
        }
    }
}
