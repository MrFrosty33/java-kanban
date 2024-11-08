import java.util.Objects;

public class Subtask {

    private final String name;
    private final int uid;
    private Status status;

    public Subtask(String name, Status status) {
        this.name = name;
        this.status = status;
        uid = hashCode();
    }

    public String getName() {
        return name;
    }

    public int getUid() {
        return uid;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return uid == subtask.uid && Objects.equals(name, subtask.name) && status == subtask.status;
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
}
