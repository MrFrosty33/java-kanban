package interfaces;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.ArrayList;

public interface TaskManager {

    /**
     * ----- Tasks -----
     */

    void addTask(Task task);

    ArrayList<Task> getAllTasks();

    Task getTask(int id);

    void updateTask(Task task);

    void removeTask(int id);

    void removeAllTasks();

    /**
     * ----- Subtasks -----
     */

    void addSubtask(Subtask subtask);

    ArrayList<Subtask> getAllSubtasks();

    Subtask getSubtask(int id);

    void updateSubtask(Subtask subtask);

    void removeSubtask(int id);

    void removeAllSubtasks();

    /**
     * ----- Epics -----
     */

    void addEpic(Epic epic);

    ArrayList<Epic> getAllEpics();

    Epic getEpic(int id);

    ArrayList<Subtask> getSubtaskIdsFromEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpic(int id);

    void removeAllEpics();

    void updateEpicStatus(int id);
}
