package models;

public class Task {

    private int id;
    private String name;
    private String description;
    private Status status;

    /**
     * ----- Constructors -----
     */

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * ----- Getter-Setter -----
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * ----- Print -----
     */

    @Override
    public String toString() {
        String taskClass = this.getClass().getName().substring(7).toUpperCase();
        String separator = ",";
        // Всё это, чтобы убрать "class models.XXX" из названия класса
        String result = getId() + separator + taskClass + separator + getName() + separator
                + getStatus() + separator + getDescription() + separator;

        return result;
    }
}
