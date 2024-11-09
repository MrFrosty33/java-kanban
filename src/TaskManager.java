import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TaskManager {

    private int nextId = 1;
    // Можно не инициализировать тут, а в конструкторе
    // с какого id продолжать. Может быть полезно при необходимости перезапуска программу
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
        ArrayList<Task> result = new ArrayList<>();
        Set<Integer> keySet = tasks.keySet();

        for (Integer key : keySet) {
            result.add(tasks.get(key));
        }
        return result;
    }

    public Task getTask(int key) {
        return tasks.get(key);
    }

    public void updateTask(Task task) {
        tasks.replace(task.getId(), task);
    }

    public void removeTask(int key) {
        tasks.remove(key);
    }

    public void removeAllTasks(){
        tasks = null;
    }

    /**
     * ----- Subtasks -----
     */

    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> result = new ArrayList<>();
        Set<Integer> keySet = subtasks.keySet();

        for (Integer key : keySet) {
            result.add(subtasks.get(key));
        }
        return result;
    }

    public Subtask getSubtask(int key) {
        return subtasks.get(key);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.replace(subtask.getId(), subtask);
    }

    public void removeSubtask(int key) {
        subtasks.remove(key);
    }

    public void removeAllSubtasks(){
        subtasks = null;
    }

    /**
     * ----- Epics -----
     */

    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epicCorrectStatus(epic);
        epics.put(epic.getId(), epic);
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> result = new ArrayList<>();
        Set<Integer> keySet = epics.keySet();

        for (Integer key : keySet) {
            result.add(epics.get(key));
        }
        return result;
    }

    public Epic getEpic(int key) {
        return epics.get(key);
    }

    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic){
        ArrayList<Subtask> result = new ArrayList<>();
        Set<Integer> subtasksKeySet = new HashSet<>(epic.getSubtasks());

        for (Integer key : subtasksKeySet) {
            result.add(subtasks.get(key));
        }

        return result;
    }

    public void updateEpic(Epic epic) {
        // при обновлении стоит перепроверить статус - вдруг внутрь поместили что-то,
        // и забыли изменить статус самого эпика
        epicCorrectStatus(epic);
        epics.replace(epic.getId(), epic);
    }

    public void removeEpic(int key) {
        epics.remove(key);
    }

    public void removeAllEpics(){
        epics = null;
    }

    // вынес в отдельный метод по причинам:
    // 1) много строчек кода, а упростить не приходит в голову как
    // 2) может использоваться в нескольких методах.
    public Epic epicCorrectStatus(Epic epic){
        if (epic.getSubtasks() == null) {
            epic.setStatus(Status.NEW);
        } else {
            Set<Integer> subtasksKeySet = new HashSet<>(epic.getSubtasks());
            boolean allNew = true;
            boolean allDone = true;

            for (Integer key : subtasksKeySet) {
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
        return epic;
    }
}
