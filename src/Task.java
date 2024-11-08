import java.util.ArrayList;
import java.util.Objects;

public class Task {

    private final String name;
    private final int uid;
    private Status status;
    private ArrayList<Subtask> subtasks;

    public Task(String name, Status status) {
        this.name = name;
        this.status = status;
        uid = hashCode();
    }

    public Task(String name, Status status, ArrayList<Subtask> subtasks) {
        this.name = name;
        this.status = status;
        this.subtasks = subtasks;
        uid = hashCode();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public int getUid() {
        return uid;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && status == task.status;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (name != null) {
            hash += name.hashCode();
        }
        hash *= 31;
        if (status != null) {
            hash += status.hashCode();
        }
        return hash;
    }

    /*
        1. Название, кратко описывающее суть задачи (например, «Переезд»).
        2. Описание, в котором раскрываются детали.
        3. Уникальный идентификационный номер задачи, по которому её можно будет найти.
        4. Статус, отображающий её прогресс. Вы будете выделять следующие этапы жизни задачи, используя enum:
            1. NEW — задача только создана, но к её выполнению ещё не приступили.
            2. IN_PROGRESS — над задачей ведётся работа.
            3. DONE — задача выполнена.

        * Для каждой подзадачи известно, в рамках какого эпика она выполняется.
        * Каждый эпик знает, какие подзадачи в него входят.
        * Завершение всех подзадач эпика считается завершением эпика.


     */
}
