package controllers;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TaskManager {

    private int nextId = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();


    /**
     * ----- Tasks -----
     */

    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public void updateTask(Task task) {
        tasks.replace(task.getId(), task);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    /**
     * ----- Subtasks -----
     */

    // узнаю, о каком эпике идёт речь через получение информации от сабтаска.
    // или же стоит передавать необходимый эпик через параметры метода?
    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.subtasks.add(subtask.getId());
        updateEpicStatus(epic.getId());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.replace(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()).getId());
    }

    public void removeSubtask(int id) {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.subtasks.remove(id);
        updateEpicStatus(epic.getId());
    }

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

    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public ArrayList<Subtask> getSubtaskIdsFromEpic(Epic epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        Set<Integer> subtasksidSet = new HashSet<>(epic.getSubtaskIds());

        for (Integer id : subtasksidSet) {
            result.add(subtasks.get(id));
        }

        return result;
    }

    public void updateEpic(Epic epic) {
        updateEpicStatus(epic.getId());
        epics.replace(epic.getId(), epic);
    }

    public void removeEpic(int id) {
        final Epic epic = epics.remove(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

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
