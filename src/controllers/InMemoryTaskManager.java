package controllers;

import comparators.StartTimeComparator;
import exceptions.ValidateTimeException;
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


    @Override
    public HistoryManager getHistory() {
        return historyManager;
    }


    /**
     * ----- Tasks -----
     */

    @Override
    public void addTask(Task task) throws ValidateTimeException {
        //TODO добавить во все методы добавления подобные исключения
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null && !validateTime(task)) prioritizedTasks.add(task);
        if(validateTime(task)) throw new ValidateTimeException();
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
        final Task task = tasks.remove(id);
        if (task == null) {
            return;
        }
        historyManager.remove(id);
        tasks.remove(id);
        prioritizedTasks.remove(tasks.get(id));
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
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
        for (Epic epic : epics.values()) {
            epic.subtasks.clear();
            updateEpic(epic);
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        prioritizedSubtasks.clear();
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
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
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
        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());
        epics.replace(epic.getId(), epic);
    }


    @Override
    public void removeEpic(int id) {
        final Epic epicToRemove = epics.remove(id);
        if (epicToRemove == null) {
            return;
        }

        epicToRemove.getSubtaskIds().stream()
                .map(subtask -> subtasks.remove(subtask))
                .filter(Objects::nonNull)
                .forEach(subtaskToRemove -> prioritizedSubtasks.remove(subtaskToRemove));

    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        epics.clear();
        prioritizedSubtasks.clear();
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

        // высчитываем продолжительность
        Duration duration = Duration.ZERO;
        for (Subtask subtask : getSubtaskIdsFromEpic(epic)) {
            if (subtask.getDuration() != null) duration = duration.plus(subtask.getDuration());
        }

        if (epic.getStartTime() != null && epic.getEndTime() != null) {
            epic.setDuration(duration);
        }
    }

    public <T extends Task> boolean validateTime(T task) {
        // если всё ок, возвращает false, если есть наложение - true

        boolean result = false;

        if (!getPrioritizedTasks().isEmpty()) {
            for (Task otherTask : getPrioritizedTasks()) {
                result = TaskValidator.validateTime(task, otherTask);
                if (result) return result;
            }
            for (Subtask otherTask : getPrioritizedSubtasks()) {
                result = TaskValidator.validateTime(task, otherTask);
                if (result) return result;
            }
        }

        return result;
    }


}
