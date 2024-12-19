package tracker;

import java.time.Instant;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(int epicId, String title, String description, Integer id, Status status) {
        super(title, description, id, status);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public Subtask(String title, int epicId,  String description, int id, Status status) {
        super(title, description, id, status);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public Subtask(int epicId, String title, String description, int id, Status status) {
        super(title, description, id, status);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public Subtask(int epicId, String title, String description, long duration, Instant startTime, int id, Status status) {
        super(id, title, description, duration, startTime, status);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public Subtask() {}

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epic id=" + getEpicId() +
                " title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + ", start time=" + getStartTime().toEpochMilli() +
                ", duration=" + getDuration() + ", end time=" + getEndTime().toEpochMilli() + '\'' + '}';
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", getEpicId(),  getId(), getType(), getTitle(),
                getStatus(), getDescription(), getStartTime(), getDuration());
    }

}
