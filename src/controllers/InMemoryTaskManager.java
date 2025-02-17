package controllers;

import comparators.StartTimeComparator;
import exceptions.NotFoundException;
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
        //правильно ли понимаю, что есть не проходит валидацию, то и добавляться \ обновляться не должен?
        //пока сделал всё по такой логике.
        if (validateTime(task)) throw new ValidateTimeException();

        task.setId(nextId++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
    }

    @Override
    public ArrayList<Task> getAllTasks() throws NotFoundException {
        // стоит ли в таких методах выбрасывать исключение?
        // пока сделал с ним
        if (tasks.isEmpty()) throw new NotFoundException();
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public Task getTask(int id) throws NotFoundException {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void updateTask(Task task) throws ValidateTimeException {
        if (validateTime(task)) throw new ValidateTimeException();

        prioritizedTasks.remove(tasks.get(task.getId()));
        tasks.replace(task.getId(), task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
    }

    @Override
    public void removeTask(int id) throws NotFoundException {
        final Task task = tasks.remove(id);
        if (task == null) throw new NotFoundException();

        historyManager.remove(id);
        tasks.remove(id);

        if (prioritizedTasks.contains(task)) {
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void removeAllTasks() {
        // если список уже пуст, стоит ли отправлять NotFoundException?
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
    public void addSubtask(Subtask subtask) throws ValidateTimeException {
        if (validateTime(subtask)) throw new ValidateTimeException();

        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.subtasks.add(subtask.getId());
        if (subtask.getStartTime() != null) prioritizedSubtasks.add(subtask);
        updateEpic(epic);
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() throws NotFoundException {
        if (subtasks.isEmpty()) throw new NotFoundException();
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getPrioritizedSubtasks() {
        return new ArrayList<>(prioritizedSubtasks);
    }


    @Override
    public Subtask getSubtask(int id) throws NotFoundException {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            historyManager.add(subtask);
            return subtask;
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ValidateTimeException {
        if (validateTime(subtask)) throw new ValidateTimeException();

        prioritizedSubtasks.remove(subtasks.get(subtask.getId()));
        Epic epic = epics.get(subtask.getEpicId());
        subtasks.replace(subtask.getId(), subtask);
        updateEpic(epic);
        if (subtask.getStartTime() != null) prioritizedSubtasks.add(subtask);
    }

    @Override
    public void removeSubtask(int id) throws NotFoundException {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) throw new NotFoundException();

        // ArrayList внутри эпика удаляет по индексу
        Epic epic = epics.get(subtask.getEpicId());
        int index = epic.subtasks.indexOf(subtask.getId());
        epic.subtasks.remove(index);

        if (prioritizedSubtasks.contains(subtask)) {
            prioritizedSubtasks.remove(subtasks.get(id));
        }
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
    public ArrayList<Epic> getAllEpics() throws NotFoundException {
        if (epics.isEmpty()) throw new NotFoundException();
        return new ArrayList<>(epics.values());
    }


    @Override
    public Epic getEpic(int id) throws NotFoundException {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic;
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) throws NotFoundException {
        Set<Integer> subtasksidSet = new HashSet<>(epic.getSubtaskIds());
        if (subtasksidSet.isEmpty()) throw new NotFoundException();

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
    public void removeEpic(int id) throws NotFoundException {
        final Epic epicToRemove = epics.remove(id);
        if (epicToRemove == null) throw new NotFoundException();

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
        if (epic.getSubtaskIds() == null || epic.getSubtaskIds().isEmpty()) {
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

        if (epic.getSubtaskIds() != null && !epic.getSubtaskIds().isEmpty() && !prioritizedSubtasks.isEmpty()) {
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
            for (Subtask subtask : getSubtasksFromEpic(epic)) {
                if (subtask.getDuration() != null) duration = duration.plus(subtask.getDuration());
            }

            if (epic.getStartTime() != null && epic.getEndTime() != null) {
                epic.setDuration(duration);
            }
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
