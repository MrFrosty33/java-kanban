package controllers;

import comparators.StartTimeComparator;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import utils.Managers;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int nextId = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(new StartTimeComparator<>());
    private final TreeSet<Subtask> prioritizedSubtasks = new TreeSet<>(new StartTimeComparator<>());
    private final TreeSet<Epic> prioritizedEpics = new TreeSet<>(new StartTimeComparator<>());


    @Override
    public HistoryManager getHistory() {
        return historyManager;
    }

    //TODO добавить во все методы деревья

    /**
     * ----- Tasks -----
     */

    @Override
    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
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
        prioritizedSubtasks.add(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.subtasks.add(subtask.getId());
        updateEpic(epic);
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getPrioritizedSubtasks() {
        return new ArrayList<>(prioritizedSubtasks);
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        subtasks.replace(subtask.getId(), subtask);
        updateEpic(epic);
    }

    @Override
    public void removeSubtask(int id) {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.subtasks.remove(id);
        updateEpic(epic);
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.subtasks.clear();
            updateEpic(epic);
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
        updateEpic(epic);
        prioritizedEpics.add(epic); // на этот момент может стоять странная дата
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Epic> getPrioritizedEoics() {
        return new ArrayList<>(prioritizedEpics);
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
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
        prioritizedEpics.remove(epic); // убираем устаревший
        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());
        epics.replace(epic.getId(), epic);
        prioritizedEpics.add(epic); // добавляем обновлённый
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

    public void updateEpicTime(int id){
        // высчитываем стартовое время
        Epic epic = epics.get(id);
        List<Subtask> prioritizedSubtasks = getPrioritizedSubtasks();
        for (Subtask subtask : prioritizedSubtasks) {
            if (subtask.getEpicId() == id) {
                epic.setStartTime(subtask.getStartTime());
                break;
            }
        }

        prioritizedSubtasks = prioritizedSubtasks.reversed();
        // высчитываем конечное время
        for (Subtask subtask : prioritizedSubtasks) {
            if (subtask.getEpicId() == id) {
                epic.setEndTime(subtask.getEndTime());
                break;
            }
        }

        if (epic.getStartTime() != null && epic.getEndTime() != null) {
            epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));
        }
    }

}
