package controllers;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import utils.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {

    private int nextId = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    @Override
    public HistoryManager getHistory() {
        return historyManager;
    }

    /**
     * ----- Tasks -----
     */

    @Override
    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.addInHistory(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        tasks.replace(task.getId(), task);
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    /**
     * ----- Subtasks -----
     */

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.subtasks.add(subtask.getId());
        updateEpicStatus(epic.getId());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addInHistory(subtask);
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.replace(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()).getId());
    }

    @Override
    public void removeSubtask(int id) {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.subtasks.remove(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.subtasks.clear();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    /**
     * ----- Epics -----
     */

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.addInHistory(epic);
        return epic;
    }

    @Override
    public ArrayList<Subtask> getSubtaskIdsFromEpic(Epic epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        Set<Integer> subtasksidSet = new HashSet<>(epic.getSubtaskIds());

        for (Integer id : subtasksidSet) {
            result.add(subtasks.get(id));
        }

        return result;
    }

    @Override
    public void updateEpic(Epic epic) {
        updateEpicStatus(epic.getId());
        epics.replace(epic.getId(), epic);
    }

    @Override
    public void removeEpic(int id) {
        final Epic epic = epics.remove(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic.getSubtaskIds() == null) {
            epic.setStatus(Status.NEW);
        } else {
            Set<Integer> subtasksidSet = new HashSet<>(epic.getSubtaskIds());
            boolean allNew = true;
            boolean allDone = true;

            for (Integer key : subtasksidSet) {
                Subtask subtask = subtasks.get(key);

                if (subtask.getStatus() != Status.NEW) {
                    allNew = false;
                }
                if (subtask.getStatus() != Status.DONE) {
                    allDone = false;
                }
            }

            if (allNew) {
                epic.setStatus(Status.NEW);
            } else if (allDone) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}
