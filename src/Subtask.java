public class Subtask extends Task {

    private int epicId;

    public Subtask(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
