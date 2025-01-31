package controllers;

import comparators.StartTimeComparator;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import utils.Managers;
import validators.TaskValidator;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

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


    /**
     * ----- Tasks -----
     */

    @Override
    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null && !validateTime(task)) prioritizedTasks.add(task);
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
        prioritizedTasks.remove(tasks.get(task.getId()));
        tasks.replace(task.getId(), task);
        if (task.getStartTime() != null && !validateTime(task)) prioritizedTasks.add(task);
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        prioritizedTasks.remove(tasks.get(id));
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        prioritizedTasks.clear();
    }

    /**
     * ----- Subtasks -----
     */

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null && !validateTime(subtask)) prioritizedSubtasks.add(subtask);

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
        prioritizedSubtasks.remove(subtasks.get(subtask.getId()));
        Epic epic = epics.get(subtask.getEpicId());
        subtasks.replace(subtask.getId(), subtask);
        updateEpic(epic);
        if (subtask.getStartTime() != null && !validateTime(subtask)) prioritizedSubtasks.add(subtask);
    }

    @Override
    public void removeSubtask(int id) {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.subtasks.remove(id);
        prioritizedSubtasks.remove(subtasks.get(id));
        updateEpic(epic);
    }

    @Override
    public void removeAllSubtasks() {
        // здесь не вижу смысла использовать stream. Будет тот же самый .forEach, только менее читаемый
        for (Epic epic : epics.values()) {
            epic.subtasks.clear();
            updateEpic(epic);
        }
        subtasks.clear();
        prioritizedSubtasks.clear();
    }

    /**
     * ----- Epics -----
     */

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        updateEpic(epic);
        if (epic.getStartTime() != null && !validateTime(epic)) prioritizedEpics.add(epic);
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Epic> getPrioritizedEpics() {
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
        Set<Integer> subtasksidSet = new HashSet<>(epic.getSubtaskIds());

        List<Subtask> result = subtasksidSet.stream()
                .map(id -> subtasks.get(id))
                .collect(Collectors.toList());

        return new ArrayList<>(result);
    }

    @Override
    public void updateEpic(Epic epic) {
        prioritizedEpics.remove(epics.get(epic.getId()));
        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());
        epics.replace(epic.getId(), epic);
        if (epic.getStartTime() != null && !validateTime(epic)) prioritizedEpics.add(epic);
    }


    @Override
    public void removeEpic(int id) {
        final Epic epicToRemove = epics.remove(id);

        epicToRemove.getSubtaskIds().stream()
                .map(subtask -> subtasks.remove(subtask))
                .filter(Objects::nonNull)
                .forEach(subtaskToRemove -> prioritizedSubtasks.remove(subtaskToRemove));

        prioritizedEpics.remove(epicToRemove);
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
        prioritizedEpics.clear();
        prioritizedSubtasks.clear();
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

            // будто тоже лучше оставить так
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

    public void updateEpicTime(int id) {
        // высчитываем стартовое время
        Epic epic = epics.get(id);
        List<Subtask> prioritizedSubtasks = getPrioritizedSubtasks();
        prioritizedSubtasks.stream()
                .filter(subtask -> subtask.getEpicId() == id)
                .findFirst()
                .ifPresent(subtask -> epic.setStartTime(subtask.getStartTime()));

        // высчитываем конечное время
        prioritizedSubtasks = prioritizedSubtasks.reversed();
        prioritizedSubtasks.stream()
                .filter(subtask -> subtask.getEpicId() == id)
                .findFirst()
                .ifPresent(subtask -> epic.setEndTime(subtask.getEndTime()));

        if (epic.getStartTime() != null && epic.getEndTime() != null) {
            epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));
        }
    }

    public <T extends Task> boolean validateTime(T task) {
        // если всё ок, возвращает false, если есть наложение - true

        // будто тут тоже не стоит переделывать на stream()
        boolean result = false;
        if (task instanceof Epic) {
            if (!getPrioritizedEpics().isEmpty()) {
                for (Epic otherTask : getPrioritizedEpics()) {
                    result = TaskValidator.validateTime(task, otherTask);
                    if (result) return result;
                }
            }
        } else if (task instanceof Subtask) {
            if (!getPrioritizedSubtasks().isEmpty()) {
                for (Subtask otherTask : getPrioritizedSubtasks()) {
                    result = TaskValidator.validateTime(task, otherTask);
                    if (result) return result;
                }
            }
        } else {
            if (!getPrioritizedTasks().isEmpty()) {
                for (Task otherTask : getPrioritizedTasks()) {
                    result = TaskValidator.validateTime(task, otherTask);
                    if (result) return result;
                }
            }
        }
        return result;
    }

}
